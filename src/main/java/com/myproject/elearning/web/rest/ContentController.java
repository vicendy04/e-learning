package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.request.ContentUpdateInput;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing contents.
 */
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Content>> createContent(@Valid @RequestBody Content content) {
        Content newContent = contentService.createContent(content);
        ApiResponse<Content> response = wrapSuccessResponse("Content created successfully", newContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Content>> getContent(@PathVariable(name = "id") Long id) {
        Content content = contentService.getContent(id);
        ApiResponse<Content> response = wrapSuccessResponse("Content retrieved successfully", content);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<Content>>> getAllContents(
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<Content> contents = contentService.getAllContents(pageable);
        ApiResponse<PagedResponse<Content>> response = wrapSuccessResponse("Contents retrieved successfully", contents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<Content>> updateContent(@RequestBody ContentUpdateInput contentUpdateInput) {
        Content updatedContent = contentService.updateContent(contentUpdateInput);
        ApiResponse<Content> response = wrapSuccessResponse("Content updated successfully", updatedContent);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable(name = "id") Long id) {
        contentService.deleteContent(id);
        ApiResponse<Void> response = wrapSuccessResponse("Content deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
