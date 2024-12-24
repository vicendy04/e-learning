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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/lessons")
@RestController
public class LessonController {
    LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<LessonGetRes>> getLesson(@PathVariable Long id) {
        LessonGetRes lesson = lessonService.getLesson(id);
        ApiRes<LessonGetRes> response = successRes("Bài học được tìm thấy", lesson);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<LessonGetRes>> editLesson(
            @PathVariable Long id, @Valid @RequestBody LessonUpdateReq request) {
        LessonGetRes updatedLesson = lessonService.editLesson(id, request);
        ApiRes<LessonGetRes> response = successRes("Cập nhật bài học thành công", updatedLesson);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> delLesson(@PathVariable Long id) {
        lessonService.delLesson(id);
        ApiRes<Void> response = successRes("Xóa bài học thành công", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
