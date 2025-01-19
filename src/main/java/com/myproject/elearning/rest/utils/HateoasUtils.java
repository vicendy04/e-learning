package com.myproject.elearning.rest.utils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.rest.course.CourseChapterController;
import com.myproject.elearning.rest.course.CourseController;
import org.springframework.stereotype.Component;

/**
 * HATEOAS: <a href="https://docs.spring.io/spring-hateoas/docs/current/reference/html/#server.link-builder.webmvc.methods">...</a>
 */
@Component
public final class HateoasUtils {
    private HateoasUtils() {}

    public static void addCourseLinks(CourseGetRes dto) {
        dto.add(
                linkTo(methodOn(CourseChapterController.class).getChaptersByCourseId(dto.getId()))
                        .withRel("chapters"),
                linkTo(methodOn(CourseChapterController.class).getExpandedChapters(dto.getId()))
                        .withRel("nested_chapters"));
    }

    public static CourseListRes addCourseListLinks(CourseListRes courseListRes) {
        courseListRes.add(linkTo(methodOn(CourseController.class).getCourse(courseListRes.getId()))
                .withSelfRel());
        return courseListRes;
    }
}
