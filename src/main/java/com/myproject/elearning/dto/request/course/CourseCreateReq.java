package com.myproject.elearning.dto.request.course;

import com.myproject.elearning.domain.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreateReq {
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    String title;

    @NotBlank(message = "Mô tả khóa học không được để trống")
    String description;

    @PositiveOrZero(message = "Thời lượng không hợp lệ")
    Integer duration;

    @PositiveOrZero(message = "Giá không hợp lệ")
    BigDecimal price;

    @NotNull(message = "Cấp độ không được để trống")
    Course.Level level;

    String thumbnailUrl;

    @NotNull(message = "Topic không được để trống")
    Long topicId;
}
