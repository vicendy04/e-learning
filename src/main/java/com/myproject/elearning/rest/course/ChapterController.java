package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
import com.myproject.elearning.service.ChapterService;
import jakarta.validation.Valid;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ApiRes<ChapterGetRes> getChapter(@PathVariable Long id) {
        ChapterGetRes chapter = chapterService.getChapter(id);
        return successRes("Chapter retrieved successfully", chapter);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#id))")
    public ApiRes<ChapterGetRes> editChapter(@PathVariable Long id, @Valid @RequestBody ChapterUpdateReq request) {
        ChapterGetRes chapter = chapterService.editChapter(id, request);
        return successRes("Chapter updated successfully", chapter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isChapterOwner(#id))")
    public ApiRes<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return successRes("Chapter deleted successfully", null);
    }
}
