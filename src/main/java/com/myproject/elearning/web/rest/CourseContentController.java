package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.content.ContentCreateReq;
import com.myproject.elearning.dto.request.content.ContentOrderReq;
import com.myproject.elearning.dto.response.content.ContentGetRes;
import com.myproject.elearning.dto.response.content.ContentListRes;
import com.myproject.elearning.service.ContentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing contents within courses
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses/{courseId}/contents")
@RestController
public class CourseContentController {
    ContentService contentService;

    @GetMapping("")
    public ResponseEntity<ApiRes<PagedRes<ContentListRes>>> getContentsByCourseId(
            @PathVariable Long courseId,
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<ContentListRes> contents = contentService.getContentsByCourseId(courseId, pageable);
        ApiRes<PagedRes<ContentListRes>> response = successRes("Contents retrieved successfully", contents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("")
    public ResponseEntity<ApiRes<Void>> delContentsOfCourse(@PathVariable Long courseId) {
        contentService.delContentsOfCourse(courseId);
        ApiRes<Void> response = successRes("Contents deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiRes<ContentGetRes>> addContentToCourse(
            @PathVariable Long courseId, @Valid @RequestBody ContentCreateReq request) {
        ContentGetRes createdContent = contentService.addContentToCourse(courseId, request);
        ApiRes<ContentGetRes> response = successRes("Content added successfully", createdContent);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Reorders the contents of a specific course.
     *
     * @param courseId            The ID of the course whose contents will be reordered.
     * @param contentOrderReq The new order of contents.
     * @return The {@link ResponseEntity} with status {@code 200 (OK)} and the reordered contents wrapped in {@link ApiRes}.
     */
    @PostMapping("/reorder")
    public ResponseEntity<ApiRes<List<ContentListRes>>> reorderContents(
            @PathVariable Long courseId, @RequestBody ContentOrderReq contentOrderReq) {
        List<ContentListRes> reorderedContents =
                contentService.reorderContents(courseId, contentOrderReq.getOrderMapping());
        ApiRes<List<ContentListRes>> response = successRes("Contents reordered successfully", reorderedContents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
