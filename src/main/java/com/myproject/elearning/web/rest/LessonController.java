package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonGetRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
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
@RequestMapping("/api/v1/lessons")
@RestController
public class LessonController {
    LessonService lessonService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<LessonGetRes> getLesson(@PathVariable Long id) {
        LessonGetRes lesson = lessonService.getLesson(id);
        return successRes("Bài học được tìm thấy", lesson);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<LessonGetRes> editLesson(@PathVariable Long id, @Valid @RequestBody LessonUpdateReq request) {
        Long instructorId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        LessonGetRes updatedLesson = lessonService.editLesson(id, request, instructorId);
        return successRes("Cập nhật bài học thành công", updatedLesson);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> delLesson(@PathVariable Long id) {
        Long instructorId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        lessonService.delLesson(id, instructorId);
        return successRes("Xóa bài học thành công", null);
    }
}
