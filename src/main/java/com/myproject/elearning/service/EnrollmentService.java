package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.response.enrollment.EnrollmentResponse;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.EmailAlreadyUsedException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.enrollment.EnrollmentMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.EnrollmentRepository;
import com.myproject.elearning.security.AuthoritiesConstants;
import com.myproject.elearning.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Transactional
    public EnrollmentResponse enrollCourse(String email, Long courseId) {
        if (enrollmentRepository.existsByUserEmailAndCourseId(email, courseId)) {
            throw new EmailAlreadyUsedException("User already enrolled in this course");
        }

        User user = userService.getUser(email);
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new InvalidIdException(courseId));

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);

        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    public void unrollCourse(String email, Long courseId) {
        Enrollment enrollment = enrollmentRepository
                .findByUserEmailAndCourseId(email, courseId)
                .orElseThrow(() -> new InvalidIdException("Enrollment not found"));
        enrollmentRepository.delete(enrollment);
    }

    public PagedResponse<EnrollmentResponse> getUserEnrollments(String email, Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentRepository.findAllByUserEmail(email, pageable);
        return PagedResponse.from(enrollments.map(enrollmentMapper::toDto));
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollment(Long enrollmentId) {
        return enrollmentMapper.toDto(
            enrollmentRepository
                .findByIdWithDetails(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId))
        );
    }

    public PagedResponse<EnrollmentResponse> getCourseEnrollments(Long courseId, Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId, pageable);
        return PagedResponse.from(enrollments.map(enrollmentMapper::toDto));
    }

    @Transactional
    public EnrollmentResponse changeEnrollmentStatus(Long enrollmentId, String newStatus) {
        Enrollment enrollment = enrollmentRepository
                .findByIdWithDetails(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId));
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(AnonymousUserException::new);
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)
                && !Objects.equals(email, enrollment.getUser().getEmail())) {
            throw new AccessDeniedException("You don't have permission to change this enrollment status");
        }
        enrollment.setStatus(Enrollment.EnrollmentStatus.valueOf(newStatus));
        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
            }

    private void validateStatusTransition(
            Enrollment.EnrollmentStatus currentStatus, Enrollment.EnrollmentStatus newStatus) {
        // Implement your status transition rules here
        if (currentStatus == Enrollment.EnrollmentStatus.COMPLETED && newStatus == Enrollment.EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot change status from COMPLETED to ACTIVE");
        }

        if ((currentStatus == Enrollment.EnrollmentStatus.DROPPED)
                && ((newStatus == Enrollment.EnrollmentStatus.ACTIVE)
                || (newStatus == Enrollment.EnrollmentStatus.COMPLETED))) {
            throw new IllegalStateException("Cannot change status from DROPPED to " + newStatus);
        }
    }
}
