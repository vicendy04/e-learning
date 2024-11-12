package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.EmailAlreadyUsedException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.EnrollmentRepository;
import com.myproject.elearning.security.AuthoritiesConstants;
import com.myproject.elearning.security.SecurityUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;

    @Transactional
    public Enrollment enrollCourse(String email, Long courseId) {
        if (enrollmentRepository.existsByUserEmailAndCourseId(email, courseId)) {
            throw new EmailAlreadyUsedException("User already enrolled in this course");
        }

        User user = userService.getUser(email);
        Course course = courseService.getCourse(courseId);

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);

        return enrollmentRepository.save(enrollment);
    }

    public void unrollCourse(String email, Long courseId) {
        Enrollment enrollment = enrollmentRepository
                .findByUserEmailAndCourseId(email, courseId)
                .orElseThrow(() -> new InvalidIdException("Enrollment not found"));
        enrollmentRepository.delete(enrollment);
    }

    public PagedResponse<Enrollment> getUserEnrollments(String email, Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentRepository.findAllByUserEmail(email, pageable);
        return PagedResponse.from(enrollments);
    }

    @Transactional(readOnly = true)
    public Enrollment getEnrollment(Long enrollmentId) {
        return enrollmentRepository
                .findByIdWithDetails(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId));
    }

    public PagedResponse<Enrollment> getCourseEnrollments(Long courseId, Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId, pageable);
        return PagedResponse.from(enrollments);
    }

    @Transactional
    public Enrollment changeEnrollmentStatus(Long enrollmentId, String newStatus) {
        Enrollment enrollment = enrollmentRepository
                .findByIdWithDetails(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId));
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(AnonymousUserException::new);
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)
                && !Objects.equals(email, enrollment.getUser().getEmail())) {
            throw new AccessDeniedException("You don't have permission to change this enrollment status");
        }
        enrollment.setStatus(Enrollment.EnrollmentStatus.valueOf(newStatus));
        return enrollmentRepository.save(enrollment);
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
