package com.myproject.elearning.service.strategy;

import com.myproject.elearning.security.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class StrategyFactory {
    CourseSearcher defaultSearcher;
    CourseSearcher personalizedSearcher;

    public CourseSearcher getStrategy() {
        Long type = userType();
        if (type > 0) {
            return personalizedSearcher;
        } else if (type == Long.MIN_VALUE) {
            return defaultSearcher;
        } else {
            throw new IllegalArgumentException("Loi roi");
        }
    }

    // custom logic here
    // ex: check if the user is in a special list,
    // and if so, provide some offers
    private Long userType() {
        return SecurityUtils.getLoginId().orElse(Long.MIN_VALUE);
    }
}
