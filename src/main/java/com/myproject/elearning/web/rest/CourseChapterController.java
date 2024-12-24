package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses/{courseId}/chapters")
@RestController
public class CourseChapterController {
    ChapterService chapterService;

    @GetMapping("")
    public ResponseEntity<ApiRes<PagedRes<ChapterGetRes>>> getChaptersByCourseId(
            @PathVariable Long courseId,
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<ChapterGetRes> chapters = chapterService.getChaptersByCourseId(courseId, pageable);
        ApiRes<PagedRes<ChapterGetRes>> response = successRes("Contents retrieved successfully", chapters);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiRes<ChapterGetRes>> addChapterToCourse(
            @PathVariable Long courseId, @Valid @RequestBody ChapterCreateReq request) {
        ChapterGetRes chapter = chapterService.addChapter(courseId, request);
        ApiRes<ChapterGetRes> response = successRes("Chapter added successfully", chapter);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("")
    public ResponseEntity<ApiRes<Void>> delChaptersOfCourse(@PathVariable Long courseId) {
        chapterService.delChaptersByCourseId(courseId);
        ApiRes<Void> response = successRes("Chapters deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
