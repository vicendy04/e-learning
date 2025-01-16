package com.myproject.elearning.rest.content;

import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.dto.search.SearchCriteria;
import com.myproject.elearning.service.CourseSearchService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses/search")
@RestController
public class SearchController {
    CourseSearchService courseSearchService;

    @GetMapping
    public PagedRes<CourseDocument> searchCourses(
            @Valid @ModelAttribute SearchCriteria criteria, @RequestParam int page, @RequestParam int size) {
        return courseSearchService.searchCourses(criteria, page, size);
    }
}
