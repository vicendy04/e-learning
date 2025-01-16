package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<PagedRes<ChapterGetRes>> getChaptersByCourseId(
            @PathVariable Long courseId,
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<ChapterGetRes> chapters = chapterService.getChaptersByCourseId(courseId, pageable);
        return successRes("Contents retrieved successfully", chapters);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#courseId))")
    public ApiRes<ChapterGetRes> addChapterToCourse(
            @PathVariable Long courseId, @Valid @RequestBody ChapterCreateReq request) {
        ChapterGetRes chapter = chapterService.addChapter(courseId, request);
        return successRes("Chapter added successfully", chapter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#courseId))")
    public ApiRes<Void> delChaptersOfCourse(@PathVariable Long courseId) {
        chapterService.delChaptersByCourseId(courseId);
        return successRes("Chapters deleted successfully", null);
    }
}
