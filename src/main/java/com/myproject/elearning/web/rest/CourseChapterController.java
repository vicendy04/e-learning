package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
import com.myproject.elearning.service.AuthzService;
import com.myproject.elearning.service.ChapterService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses/{courseId}/chapters")
@RestController
public class CourseChapterController {
    ChapterService chapterService;
    AuthzService authzService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<ChapterGetRes>> getChaptersByCourseId(
            @PathVariable Long courseId,
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<ChapterGetRes> chapters = chapterService.getChaptersByCourseId(courseId, pageable);
        return successRes("Contents retrieved successfully", chapters);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiRes<ChapterGetRes> addChapterToCourse(
            @PathVariable Long courseId, @Valid @RequestBody ChapterCreateReq request) {
        authzService.checkCourseAccess(courseId);
        ChapterGetRes chapter = chapterService.addChapter(courseId, request);
        return successRes("Chapter added successfully", chapter);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<ChapterGetRes> editChapter(
            @PathVariable Long courseId, @PathVariable Long id, @Valid @RequestBody ChapterUpdateReq request) {
        authzService.checkCourseAccess(courseId);
        ChapterGetRes chapter = chapterService.editChapter(id, request);
        return successRes("Chapter updated successfully", chapter);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> deleteChapter(@PathVariable Long courseId, @PathVariable Long id) {
        authzService.checkCourseAccess(courseId);
        chapterService.deleteChapter(id);
        return successRes("Chapter deleted successfully", null);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> delChaptersOfCourse(@PathVariable Long courseId) {
        authzService.checkCourseAccess(courseId);
        chapterService.delChaptersByCourseId(courseId);
        return successRes("Chapters deleted successfully", null);
    }
}
