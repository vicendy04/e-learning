package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonRes;
import com.myproject.elearning.service.LessonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/lessons/{lessonId}")
@RestController
public class LessonController {
    LessonService lessonService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<LessonRes> getLesson(@PathVariable Long lessonId) {
        LessonRes lesson = lessonService.getLesson(lessonId);
        return successRes("Bài học được tìm thấy", lesson);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isLessonOwner(#lessonId))")
    public ApiRes<LessonRes> editLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonUpdateReq request) {
        LessonRes updatedLesson = lessonService.editLesson(lessonId, request);
        return successRes("Cập nhật bài học thành công", updatedLesson);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isLessonOwner(#lessonId))")
    public ApiRes<Void> delLesson(@PathVariable Long lessonId) {
        lessonService.delLesson(lessonId);
        return successRes("Xóa bài học thành công", null);
    }
}
