package com.myproject.elearning.constant;

/**
 * Referenced from {@link org.springframework.transaction.annotation.Propagation}
 */
public enum ResourceType {
    COURSE("course"),
    CHAPTER("chapter"),
    LESSON("lesson");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
