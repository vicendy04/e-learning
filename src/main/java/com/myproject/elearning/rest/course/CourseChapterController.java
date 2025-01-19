package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.response.chapter.ChapterRes;
import com.myproject.elearning.dto.response.chapter.ExpandedChapterRes;
import com.myproject.elearning.service.ChapterService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public ApiRes<List<ChapterRes>> getChaptersByCourseId(@PathVariable Long courseId) {
        List<ChapterRes> chapters = chapterService.getChaptersByCourseId(courseId);
        return successRes("Contents retrieved successfully", chapters);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/expanded")
    public ApiRes<List<ExpandedChapterRes>> getExpandedChapters(@PathVariable Long courseId) {
        List<ExpandedChapterRes> chapters = chapterService.getExpandedChapters(courseId);
        return successRes("Retrieved successfully", chapters);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#courseId))")
    public ApiRes<ChapterRes> addChapterToCourse(
            @PathVariable Long courseId, @Valid @RequestBody ChapterCreateReq request) {
        ChapterRes chapter = chapterService.addChapter(courseId, request);
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
