package com.myproject.elearning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResultPaginated;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.dto.search.SearchCriteria;
import com.myproject.elearning.mapper.CourseSearchMapper;
import com.myproject.elearning.repository.specification.CourseQueryBuilder;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Repository
public class CourseSearchRepository {
    Index courseIndex;
    ObjectMapper objectMapper;
    CourseSearchMapper courseSearchMapper;

    public PagedRes<CourseDocument> search(SearchCriteria criteria, int page, int size) {
        SearchRequest searchRequest = CourseQueryBuilder.buildQuery(criteria, page, size);
        SearchResultPaginated response = (SearchResultPaginated) courseIndex.search(searchRequest);

        List<CourseDocument> courseDocuments = response.getHits().stream()
                .map(hit -> objectMapper.convertValue(hit, CourseDocument.class))
                .toList();

        return PagedRes.from(response, courseDocuments);
    }

    public void indexCourse(Course course) {
        try {
            CourseDocument doc = courseSearchMapper.toCourseDocument(course);
            courseIndex.addDocuments(objectMapper.writeValueAsString(doc));
        } catch (Exception e) {
            throw new RuntimeException("Error indexing course", e);
        }
    }
}
