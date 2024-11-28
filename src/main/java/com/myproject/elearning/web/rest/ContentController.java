package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.content.ContentCreateRequest;
import com.myproject.elearning.dto.request.content.ContentUpdateRequest;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.service.ContentService;
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

/**
 * REST controller for managing contents.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/contents")
@RestController
public class ContentController {
    ContentService contentService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<ContentGetResponse>> createContent(
            @Valid @RequestBody ContentCreateRequest request) {
        ContentGetResponse newContent = contentService.createContent(request);
        ApiResponse<ContentGetResponse> response = wrapSuccessResponse("Content created successfully", newContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentGetResponse>> getContent(@PathVariable(name = "id") Long id) {
        ContentGetResponse content = contentService.getContent(id);
        ApiResponse<ContentGetResponse> response = wrapSuccessResponse("Content retrieved successfully", content);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<ContentGetResponse>>> getAllContents(
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<ContentGetResponse> contents = contentService.getAllContents(pageable);
        ApiResponse<PagedResponse<ContentGetResponse>> response =
                wrapSuccessResponse("Contents retrieved successfully", contents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentGetResponse>> updateContent(
            @PathVariable(name = "id") Long id, @Valid @RequestBody ContentUpdateRequest request) {
        ContentGetResponse updatedContent = contentService.updateContent(id, request);
        ApiResponse<ContentGetResponse> response = wrapSuccessResponse("Content updated successfully", updatedContent);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable(name = "id") Long id) {
        contentService.deleteContent(id);
        ApiResponse<Void> response = wrapSuccessResponse("Content deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
