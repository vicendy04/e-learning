package com.myproject.elearning.repository.specification;

import com.meilisearch.sdk.SearchRequest;
import com.myproject.elearning.dto.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * See: <a href="https://www.meilisearch.com/docs/guides/front_end/pagination">...</a>
 * See: <a href="https://medium.com/@abdel.elamel/blazing-fast-searches-with-meilisearch-a-comprehensive-guide-d504f0e1fd6f">...</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CourseQueryBuilder {
    public static SearchRequest buildQuery(SearchCriteria criteria, int page, int size) {
        return SearchRequest.builder()
                .q(criteria.getQuery())
                .page(page + 1)
                .hitsPerPage(size)
                .filter(buildFilters(criteria))
                .build();
    }

    private static String[] buildFilters(SearchCriteria criteria) {
        List<String> filters = new ArrayList<>();
        categoryFilter(criteria, filters);
        levelFilter(criteria, filters);
        durationFilter(criteria, filters);
        priceFilter(criteria, filters);

        return filters.toArray(new String[0]);
    }

    private static void categoryFilter(SearchCriteria criteria, List<String> filters) {
        if (criteria.getCategory() != null) {
            filters.add(String.format("category = '%s'", criteria.getCategory()));
        }
    }

    private static void levelFilter(SearchCriteria criteria, List<String> filters) {
        if (criteria.getLevel() != null) {
            filters.add(String.format("level = '%s'", criteria.getLevel()));
        }
    }

    private static void durationFilter(SearchCriteria criteria, List<String> filters) {
        if (criteria.getMinDuration() != null) {
            filters.add(String.format("duration >= %d", criteria.getMinDuration()));
        }

        if (criteria.getMaxDuration() != null
                && (criteria.getMinDuration() == null || criteria.getMaxDuration() >= criteria.getMinDuration())) {
            filters.add(String.format("duration <= %d", criteria.getMaxDuration()));
        }
    }

    private static void priceFilter(SearchCriteria criteria, List<String> filters) {
        if (criteria.getMinPrice() != null) {
            filters.add(String.format("price >= %f", criteria.getMinPrice()));
        }

        if (criteria.getMaxPrice() != null
                && (criteria.getMinPrice() == null || criteria.getMaxPrice() >= criteria.getMinPrice())) {
            filters.add(String.format("price <= %f", criteria.getMaxPrice()));
        }
    }
}
