package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.response.lesson.LessonGetRes;
import com.myproject.elearning.dto.response.lesson.LessonListRes;
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
@RequestMapping("/api/v1/chapters/{chapterId}/lessons")
@RestController
public class ChapterLessonController {
    LessonService lessonService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<List<LessonListRes>> getLessonsByChapterId(@PathVariable Long chapterId) {
        List<LessonListRes> lessons = lessonService.getLessonsByChapterId(chapterId);
        return successRes("Danh sách bài học", lessons);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#chapterId))")
    public ApiRes<LessonGetRes> addLessonToChapter(
            @PathVariable Long chapterId, @Valid @RequestBody LessonCreateReq request) {
        LessonGetRes createdLesson = lessonService.addLessonToChapter(chapterId, request);
        return successRes("Thêm bài học thành công", createdLesson);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#chapterId))")
    public ApiRes<Void> delLessonsOfChapter(@PathVariable Long chapterId) {
        lessonService.delLessonsByChapterId(chapterId);
        return successRes("Lessons deleted successfully", null);
    }
}
