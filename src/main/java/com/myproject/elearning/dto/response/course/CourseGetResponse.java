package com.myproject.elearning.dto.response.course;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseGetResponse {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private BigDecimal price;
    private String category;
    private int enrollmentCount;
    private List<ContentDTO> contents;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentDTO {
        private Long id;
        private String title;
        private int orderIndex;
        private String status;
    }
}
