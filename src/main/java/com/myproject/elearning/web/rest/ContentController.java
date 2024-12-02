package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.content.ContentUpdateReq;
import com.myproject.elearning.dto.response.content.ContentGetRes;
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<ContentGetRes>> getContent(@PathVariable(name = "id") Long id) {
        ContentGetRes content = contentService.getContent(id);
        ApiRes<ContentGetRes> response = successRes("Content retrieved successfully", content);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiRes<PagedRes<ContentGetRes>>> getContents(
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<ContentGetRes> contents = contentService.getContents(pageable);
        ApiRes<PagedRes<ContentGetRes>> response = successRes("Contents retrieved successfully", contents);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<ContentGetRes>> editContent(
            @PathVariable(name = "id") Long id, @Valid @RequestBody ContentUpdateReq request) {
        ContentGetRes updatedContent = contentService.editContent(id, request);
        ApiRes<ContentGetRes> response = successRes("Content updated successfully", updatedContent);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> delContent(@PathVariable(name = "id") Long id) {
        contentService.delContent(id);
        ApiRes<Void> response = successRes("Content deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
