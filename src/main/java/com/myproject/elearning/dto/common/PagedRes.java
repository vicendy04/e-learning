package com.myproject.elearning.dto.common;

import com.meilisearch.sdk.model.SearchResultPaginated;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

/**
 * Custom response for paginated.
 *
 * @param <T> the type of the content in the page
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagedRes<T> {
    int page;
    int size;
    long totalElements;
    int totalPages;
    int processingTimeMs;
    List<T> content;

    public static <T> PagedRes<T> from(Page<T> page) {
        return PagedRes.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .processingTimeMs(0)
                .content(page.getContent())
                .build();
    }

    public static <T> PagedRes<T> from(SearchResultPaginated response, List<T> data) {
        return PagedRes.<T>builder()
                .page(response.getPage())
                .size(response.getHitsPerPage())
                .totalElements(response.getTotalHits())
                .totalPages(response.getTotalPages())
                .processingTimeMs(response.getProcessingTimeMs())
                .content(data)
                .build();
    }
}
