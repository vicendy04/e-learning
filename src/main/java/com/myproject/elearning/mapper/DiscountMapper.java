package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.request.course.CourseUpdateRequest;
import com.myproject.elearning.dto.request.discount.DiscountCreateRequest;
import com.myproject.elearning.dto.response.course.CourseUpdateResponse;
import com.myproject.elearning.dto.response.discount.DiscountGetResponse;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.Collections;
import java.util.HashSet;
import org.mapstruct.*;

@Mapper(
        config = MapperConfig.class,
        imports = {HashSet.class, Collections.class})
public interface DiscountMapper {

    // Create operations
    @Mapping(target = "id", ignore = true) // fix DiscountCreateMapperImpl
    //    @Mapping(target = "instructorId", source = "instructorId")
    @Mapping(
            target = "specificCourseIds",
            expression =
                    "java(dto.getAppliesTo() == Discount.DiscountAppliesTo.SPECIFIC ? new HashSet<>(dto.getSpecificCourseIds()) : Collections.EMPTY_SET)")
    Discount toEntity(DiscountCreateRequest dto, Long instructorId);

    // Update operations
    Course toEntity(CourseUpdateRequest request);

    CourseUpdateResponse toUpdateResponse(Course entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Course entity, CourseUpdateRequest request);

    // Get operations
    // @Mapping(target = "enrollmentCount", expression = "java(course.getEnrollments().size())")
    //    @Mapping(target = "contents", expression = "java(mapContents(course.getContents()))")
    DiscountGetResponse toGetResponse(Discount entity);

    // List operations
}
