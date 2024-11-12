package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.request.ContentOrderInput;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.ContentListResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.service.ContentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing contents within courses
 */
@RestController
@RequestMapping("/api/v1/courses/{courseId}/contents")
@RequiredArgsConstructor
public class CourseContentController {
    private final ContentService contentService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<ContentListResponse>>> getContentsByCourseId(
            @PathVariable Long courseId,
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<ContentListResponse> contents = contentService.getContentsByCourseId(courseId, pageable);
        ApiResponse<PagedResponse<ContentListResponse>> response =
                wrapSuccessResponse("Contents retrieved successfully", contents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("")
    public ResponseEntity<ApiResponse<Void>> deleteContentsOfCourse(@PathVariable Long courseId) {
        contentService.deleteContentsOfCourse(courseId);
        ApiResponse<Void> response = wrapSuccessResponse("Contents deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Content>> addContentToCourse(
            @PathVariable Long courseId, @RequestBody Content content) {
        Content createdContent = contentService.addContentToCourse(courseId, content);
        ApiResponse<Content> response = wrapSuccessResponse("Content added successfully", createdContent);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Reorders the contents of a specific course.
     *
     * @param courseId          The ID of the course whose contents will be reordered.
     * @param contentOrderInput The new order of contents.
     * @return The {@link ResponseEntity} with status {@code 200 (OK)} and the reordered contents wrapped in {@link ApiResponse}.
     */
    @PostMapping("/reorder")
    public ResponseEntity<ApiResponse<List<Content>>> reorderContents(
            @PathVariable Long courseId, @RequestBody ContentOrderInput contentOrderInput) {
        List<Content> reorderedContents = contentService.reorderContents(courseId, contentOrderInput.getOrderMapping());
        ApiResponse<List<Content>> response = wrapSuccessResponse("Contents reordered successfully", reorderedContents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
