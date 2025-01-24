package com.myproject.elearning.service.strategy;

import static com.myproject.elearning.constant.UserType.DEFAULT;
import static com.myproject.elearning.constant.UserType.PERSONALIZED;

import com.myproject.elearning.constant.UserType;
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
        var userType = userType();
        return switch (userType) {
            case PERSONALIZED -> personalizedSearcher;
            case DEFAULT -> defaultSearcher;
        };
    }

    // custom logic here
    // ex: check if the user is in a special list,
    // and if so, provide some offers
    private static UserType userType() {
        return SecurityUtils.getLoginId().isPresent() ? PERSONALIZED : DEFAULT;
    }
}
