package com.myproject.elearning.service.dto.response;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Custom response for paginated.
 *
 * @param <T> the type of the content in the page
 */
@Getter
@Setter
public class PagedResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<T> content;

    private PagedResponse() {}

    public static <T> PagedResponse<T> from(Page<T> page) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        List<T> contentInPage = page.getNumberOfElements() > 0 ? page.getContent() : Collections.emptyList();
        response.setContent(contentInPage);
        return response;
    }
}
