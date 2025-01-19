package com.myproject.elearning.service;

import static com.myproject.elearning.domain.Enrollment.EnrollmentStatus;
import static com.myproject.elearning.mapper.EnrollmentMapper.ENROLLMENT_MAPPER;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.enrollment.EnrollStatusUpdateReq;
import com.myproject.elearning.dto.response.enrollment.EnrollmentEditRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.exception.problemdetails.EmailUsedEx;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.EnrollmentRepository;
import com.myproject.elearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class EnrollService {
    EnrollmentRepository enrollmentRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;

    @Transactional
    public EnrollmentRes enrollCourse(Long courseId, Long userId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EmailUsedEx("Bạn đã đăng ký khóa học này rồi");
        }
        User userRef = userRepository.getReferenceById(userId);
        Course courseRef = courseRepository.getReferenceById(courseId);
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(userRef);
        enrollment.setCourse(courseRef);
        // đảm bảo tính atomic nhưng có thể gây ra bottleneck khi có nhiều concurrent requests
        courseRepository.incrementEnrollmentCount(courseId);
        Enrollment save = enrollmentRepository.save(enrollment);
        return ENROLLMENT_MAPPER.toRes(save);
    }

    @Transactional
    public void unrollCourse(Long courseId, Long userId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new InvalidIdEx("Không tìm thấy đăng ký khóa học");
        }
        enrollmentRepository.deleteByUserIdAndCourseId(userId, courseId);
        courseRepository.decrementEnrollmentCount(courseId);
    }

    @Transactional(readOnly = true)
    public PagedRes<EnrollmentGetRes> getMyEnrollments(Pageable pageable, Long userId) {
        Page<Enrollment> enrollments = enrollmentRepository.getPagedEnrollmentsByUserId(userId, pageable);
        return PagedRes.from(enrollments.map(ENROLLMENT_MAPPER::toGetRes));
    }

    @Transactional(readOnly = true)
    public EnrollmentGetRes getEnrollment(Long enrollmentId) {
        Enrollment enrollment =
                enrollmentRepository.findByIdWithDetails(enrollmentId).orElseThrow(() -> new InvalidIdEx(enrollmentId));
        return ENROLLMENT_MAPPER.toGetRes(enrollment);
    }

    @Transactional
    public EnrollmentEditRes changeEnrollStatus(Long enrollmentId, EnrollStatusUpdateReq input) {
        Enrollment enrollment =
                enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new InvalidIdEx(enrollmentId));
        validateStatusTransition(enrollment, input);
        enrollment.setStatus(input.getStatus());
        enrollment.setReasonForDropping(input.getReasonForDropping());
        return ENROLLMENT_MAPPER.toEditRes(enrollmentRepository.save(enrollment));
    }

    private void validateStatusTransition(Enrollment enrollment, EnrollStatusUpdateReq input) {
        EnrollmentStatus currentStatus = enrollment.getStatus();
        EnrollmentStatus newStatus = input.getStatus();
        if (currentStatus == EnrollmentStatus.COMPLETED && newStatus == EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot change status from COMPLETED to ACTIVE");
        }

        if ((currentStatus == EnrollmentStatus.DROPPED)
                && ((newStatus == EnrollmentStatus.ACTIVE) || (newStatus == EnrollmentStatus.COMPLETED))) {
            throw new IllegalStateException("Cannot change status from DROPPED to " + newStatus);
        }
    }
}
