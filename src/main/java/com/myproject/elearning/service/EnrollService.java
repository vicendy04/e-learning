package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.exception.problemdetails.EmailAlreadyUsedException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.EnrollmentMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.EnrollmentRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.AuthoritiesConstants;
import com.myproject.elearning.security.SecurityUtils;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class EnrollService {
    EnrollmentRepository enrollmentRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    EnrollmentMapper enrollmentMapper;

    @Transactional
    public EnrollmentRes enrollCourse(Long courseId, Long userId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EmailAlreadyUsedException("Bạn đã đăng ký khóa học này rồi");
        }
        User userRef = userRepository.getReferenceIfExists(userId);
        Course courseRef = courseRepository.getReferenceIfExists(courseId);
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(userRef);
        enrollment.setCourse(courseRef);
        Enrollment save = enrollmentRepository.save(enrollment);
        courseRepository.incrementEnrollmentCount(courseId);
        return enrollmentMapper.toEnrollmentResponse(save);
    }

    @Transactional
    public void unrollCourse(Long courseId, Long userId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new InvalidIdException("Không tìm thấy đăng ký khóa học");
        }
        enrollmentRepository.deleteByUserIdAndCourseId(userId, courseId);
        courseRepository.decrementEnrollmentCount(courseId);
    }

    public PagedRes<EnrollmentGetRes> getUserEnrollments(Pageable pageable, Long userId) {
        Page<Enrollment> enrollments = enrollmentRepository.findAllByUserId(userId, pageable);
        return PagedRes.from(enrollments.map(enrollmentMapper::toEnrollmentGetResponse));
    }

    @Transactional(readOnly = true)
    public EnrollmentGetRes getEnrollment(Long enrollmentId, Long userId) {
        Enrollment enrollment = enrollmentRepository
                .findByIdWithDetails(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId));

        // Todo: check sql if it triggers 1 + n problem
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)
                && !Objects.equals(userId, enrollment.getUser().getId())) {
            throw new AccessDeniedException("Bạn không có quyền xem chi tiết đăng ký này");
        }

        return enrollmentMapper.toEnrollmentGetResponse(enrollment);
    }

    public PagedRes<EnrollmentGetRes> getCourseEnrollments(Long courseId, Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId, pageable);
        return PagedRes.from(enrollments.map(enrollmentMapper::toEnrollmentGetResponse));
    }

    @Transactional
    public EnrollmentGetRes changeEnrollStatus(Long enrollmentId, String newStatus, Long userId) {
        Enrollment enrollment = enrollmentRepository
                .findByIdWithDetails(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId));

        // Todo: check sql if it triggers 1 + n problem
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)
                && !Objects.equals(userId, enrollment.getUser().getId())) {
            throw new AccessDeniedException("Bạn không có quyền thay đổi trạng thái đăng ký này");
        }

        Enrollment.EnrollmentStatus currentStatus = enrollment.getStatus();
        Enrollment.EnrollmentStatus targetStatus = Enrollment.EnrollmentStatus.valueOf(newStatus);
        validateStatusTransition(currentStatus, targetStatus);
        enrollment.setStatus(targetStatus);
        return enrollmentMapper.toEnrollmentGetResponse(enrollmentRepository.save(enrollment));
    }

    private void validateStatusTransition(
            Enrollment.EnrollmentStatus currentStatus, Enrollment.EnrollmentStatus newStatus) {
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
