package com.myproject.elearning.domain.enums;

import java.util.Set;

/**
 * <a href="https://www.baeldung.com/java-enum-simple-state-machine">...</a>
 */
public enum EnrollmentStatus {
    ACTIVE,
    COMPLETED,
    DROPPED,
    REFUNDED,
    EXPIRED;

    public Set<EnrollmentStatus> getInvalidTransitions() {
        return switch (this) {
            case COMPLETED -> Set.of(ACTIVE);
            case DROPPED -> Set.of(ACTIVE, COMPLETED);
            default -> Set.of();
        };
    }
}
