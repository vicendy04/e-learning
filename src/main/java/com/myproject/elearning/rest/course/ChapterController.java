package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.response.chapter.ChapterRes;
import com.myproject.elearning.dto.response.lesson.LessonRes;
import com.myproject.elearning.service.ChapterService;
import com.myproject.elearning.service.LessonService;
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
@RequestMapping("/api/v1/chapters/{chapterId}/")
@RestController
public class ChapterController {
    ChapterService chapterService;
    LessonService lessonService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<ChapterRes> getChapter(@PathVariable Long chapterId) {
        var chapter = chapterService.getChapter(chapterId);
        return successRes("Chapter retrieved successfully", chapter);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#chapterId))")
    public ApiRes<ChapterRes> editChapter(@PathVariable Long chapterId, @Valid @RequestBody ChapterUpdateReq request) {
        var editedChapter = chapterService.editChapter(chapterId, request);
        return successRes("Chapter updated successfully", editedChapter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#chapterId))")
    public ApiRes<Void> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return successRes("Chapter deleted successfully", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/lessons")
    public ApiRes<List<LessonRes>> getLessonsByChapterId(@PathVariable Long chapterId) {
        var lessons = lessonService.getLessonsByChapterId(chapterId);
        return successRes("Danh sách bài học", lessons);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/lessons")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#chapterId))")
    public ApiRes<LessonRes> addLessonToChapter(
            @PathVariable Long chapterId, @Valid @RequestBody LessonCreateReq request) {
        var newLesson = lessonService.addLessonToChapter(chapterId, request);
        return successRes("Thêm bài học thành công", newLesson);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/lessons")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#chapterId))")
    public ApiRes<Void> delLessonsOfChapter(@PathVariable Long chapterId) {
        lessonService.delLessonsByChapterId(chapterId);
        return successRes("Lessons deleted successfully", null);
    }
}
