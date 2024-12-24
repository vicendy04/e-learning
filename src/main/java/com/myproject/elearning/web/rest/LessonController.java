package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonGetRes;
import com.myproject.elearning.service.LessonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<LessonGetRes> editLesson(@PathVariable Long id, @Valid @RequestBody LessonUpdateReq request) {
        LessonGetRes updatedLesson = lessonService.editLesson(id, request);
        return successRes("Cập nhật bài học thành công", updatedLesson);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> delLesson(@PathVariable Long id) {
        lessonService.delLesson(id);
        return successRes("Xóa bài học thành công", null);
    }
}
