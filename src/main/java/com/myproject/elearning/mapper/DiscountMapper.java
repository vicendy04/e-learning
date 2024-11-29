package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.request.discount.DiscountCreateReq;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.dto.response.discount.DiscountGetRes;
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
    Discount toEntity(DiscountCreateReq dto, Long instructorId);

    // Update operations
    Course toEntity(CourseUpdateReq request);

    CourseUpdateRes toUpdateResponse(Course entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Course entity, CourseUpdateReq request);

    // Get operations
    // @Mapping(target = "enrollmentCount", expression = "java(course.getEnrollments().size())")
    //    @Mapping(target = "contents", expression = "java(mapContents(course.getContents()))")
    DiscountGetRes toGetResponse(Discount entity);

    // List operations
}
