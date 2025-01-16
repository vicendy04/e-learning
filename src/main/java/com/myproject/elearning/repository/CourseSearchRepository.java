package com.myproject.elearning.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResultPaginated;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.dto.search.SearchCriteria;
import com.myproject.elearning.mapper.CourseMapper;
import com.myproject.elearning.mapper.CourseSearchMapper;
import com.myproject.elearning.repository.specification.CourseQueryBuilder;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Repository
public class CourseSearchRepository {
    Index courseIndex;
    ObjectMapper objectMapper;
    CourseSearchMapper courseSearchMapper;
    CourseRepository courseRepository;
    CourseMapper courseMapper;

    public PagedRes<CourseDocument> search(SearchCriteria criteria, int page, int size) {
        SearchRequest searchRequest = CourseQueryBuilder.buildQuery(criteria, page, size);
        SearchResultPaginated response = (SearchResultPaginated) courseIndex.search(searchRequest);

        List<CourseDocument> courseDocuments = response.getHits().stream()
                .map(hit -> objectMapper.convertValue(hit, CourseDocument.class))
                .toList();

        return PagedRes.from(response, courseDocuments);
    }

    public void indexCourse(CourseGetRes course) throws JsonProcessingException {
        CourseDocument doc = courseSearchMapper.toCourseDocument(course);
        courseIndex.addDocuments(objectMapper.writeValueAsString(doc)); // => { "taskUid": 0 }
    }

    // I don't mind the 1 + n problem due to convenience
    @Transactional
    public void setupMeilisearch() throws JsonProcessingException {
        List<CourseGetRes> courses =
                courseRepository.findAll().stream().map(courseMapper::toGetRes).toList();

        List<CourseDocument> documents =
                courses.stream().map(courseSearchMapper::toCourseDocument).toList();

        var task = courseIndex.addDocuments(objectMapper.writeValueAsString(documents), "id");
        System.out.println("TaskUID: " + task.getTaskUid());
    }
}
