package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
import com.myproject.elearning.service.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/chapters")
@RestController
public class ChapterController {
    ChapterService chapterService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<ChapterGetRes>> getChapter(@PathVariable Long id) {
        ChapterGetRes chapter = chapterService.getChapter(id);
        ApiRes<ChapterGetRes> response = successRes("Chapter retrieved successfully", chapter);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<ChapterGetRes>> editChapter(
            @PathVariable Long id, @RequestBody ChapterUpdateReq request) {
        ChapterGetRes chapter = chapterService.editChapter(id, request);
        ApiRes<ChapterGetRes> response = successRes("Chapter updated successfully", chapter);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        ApiRes<Void> response = successRes("Chapter deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
