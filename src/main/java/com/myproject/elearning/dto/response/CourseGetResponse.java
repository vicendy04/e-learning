package com.myproject.elearning.dto.response;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseGetResponse {
    private Long id;
    private String title;
    private String description;
    private int duration;
    private BigDecimal price;
    private String category;
    private List<ContentDTO> contents;

    @Getter
    @Setter
    public static class ContentDTO {
        private Long id;
        private String title;
        private int orderIndex;
    }

    public static CourseGetResponse toDTO(Course course) {
        CourseGetResponse courseGetResponse = new CourseGetResponse();
        courseGetResponse.setId(course.getId());
        courseGetResponse.setTitle(course.getTitle());
        courseGetResponse.setDescription(course.getDescription());
        courseGetResponse.setDuration(course.getDuration());
        courseGetResponse.setPrice(course.getPrice());
        courseGetResponse.setCategory(course.getCategory());

        if (course.getContents() != null) {
            courseGetResponse.setContents(course.getContents().stream()
                    .map(content -> {
                        ContentDTO contentDTO = new ContentDTO();
                        contentDTO.setId(content.getId());
                        contentDTO.setTitle(content.getTitle());
                        contentDTO.setOrderIndex(content.getOrderIndex());
                        return contentDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return courseGetResponse;
    }
}
