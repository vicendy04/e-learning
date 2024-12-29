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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/chapters")
@RestController
public class ChapterController {
    ChapterService chapterService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<ChapterGetRes> getChapter(@PathVariable Long id) {
        ChapterGetRes chapter = chapterService.getChapter(id);
        return successRes("Chapter retrieved successfully", chapter);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<ChapterGetRes> editChapter(@PathVariable Long id, @RequestBody ChapterUpdateReq request) {
        ChapterGetRes chapter = chapterService.editChapter(id, request);
        return successRes("Chapter updated successfully", chapter);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return successRes("Chapter deleted successfully", null);
    }
}
