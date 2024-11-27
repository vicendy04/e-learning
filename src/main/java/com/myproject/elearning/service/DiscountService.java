package com.myproject.elearning.service;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.discount.DiscountCreateRequest;
import com.myproject.elearning.dto.response.discount.DiscountGetResponse;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidDiscountException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.DiscountMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.CourseRepository.CourseForValidDiscount;
import com.myproject.elearning.repository.DiscountRepository;
import com.myproject.elearning.security.AuthoritiesConstants;
import com.myproject.elearning.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final CourseRepository courseRepository;
    private final DiscountMapper discountMapper;

    @Transactional
    public String generateDiscountCode(DiscountCreateRequest request) {
        Long instructorId = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        if (discountRepository.existsByInstructorIdAndDiscountCode(instructorId, request.getDiscountCode())) {
            throw new InvalidDiscountException("Mã giảm giá đã tồn tại");
        }

        if (request.getAppliesTo() == Discount.DiscountAppliesTo.SPECIFIC) {
            Set<Long> instructorCourseIds = courseRepository.findCourseIdsByInstructorId(instructorId);

            if (!instructorCourseIds.containsAll(request.getSpecificCourseIds())) {
                throw new InvalidDiscountException("Một số khóa học không thuộc về bạn");
            }
        }

        Discount discount = discountMapper.toEntity(request, instructorId);
        discountRepository.save(discount);
        return request.getDiscountCode();
    }

    public PagedResponse<DiscountGetResponse> getDiscountsForInstructor(Pageable pageable) {
        Long id = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        Page<DiscountGetResponse> discounts = Page.empty();
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.INSTRUCTOR)) {
            discounts = discountRepository.findAllByInstructorId(pageable, id).map(discountMapper::toGetResponse);
        }
        return PagedResponse.from(discounts);
    }

    //    3. Get all product by discount code [User/Student]
    //        public PagedResponse<CourseGetResponse> getCoursesByDiscountCode(Pageable pageable, String discountCode) {
    //
    //            Discount discount = discountRepository.findByDiscountCode(discountCode)
    //                    .orElseThrow(() -> new InvalidDiscountCodeException("Mã giảm giá không tồn tại"));
    //
    //            if (!discount.getIsActive()) {
    //                throw new InvalidDiscountCodeException("Mã giảm giá đã hết hạn hoặc không còn hiệu lực");
    //            }
    //
    //            if (discount.getAppliesTo() == Discount.DiscountAppliesTo.ALL) {
    //                // Lấy tất cả khóa học của instructor
    //                return courseService.getAllCoursesByInstructor(discount.getInstructor().getId());
    //            } else {
    //                // Lấy các khóa học cụ thể
    //                return courseService.getAllCoursesByIds(new ArrayList<>(discount.getSpecificCourseIds()));
    //            }
    //        }

    public boolean validateDiscountForCourse(String discountCode, Long courseId) {
        Discount discount = discountRepository
                .findByDiscountCode(discountCode)
                .orElseThrow(() -> new InvalidDiscountException("Mã giảm giá không tồn tại"));
        CourseForValidDiscount course =
                courseRepository.findCourseWithInstructor(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        return isApplicableToCourse(discount, course);
    }

    private boolean isApplicableToCourse(Discount discount, CourseForValidDiscount course) {
        if (!discount.getIsActive()
                || LocalDateTime.now().isBefore(discount.getStartDate())
                || LocalDateTime.now().isAfter(discount.getEndDate())) {
            return false;
        }
        if (discount.getUsesCount() >= discount.getMaxUses()) {
            return false;
        }
        if (course.getPrice().compareTo(discount.getMinOrderValue()) < 0) {
            return false;
        }
        if (!course.getInstructorId().equals(discount.getInstructorId())) {
            return false;
        }
        if (discount.getAppliesTo() == Discount.DiscountAppliesTo.ALL) {
            return true;
        }
        return discount.getSpecificCourseIds().contains(course.getId());
    }

    @Transactional
    public void deleteDiscountCode(Long discountId) {
        Long id = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        int rowsDeleted = discountRepository.deleteByIdAndInstructorId(discountId, id);
        if (rowsDeleted == 0) {
            throw new InvalidDiscountException("Mã giảm giá không tồn tại hoặc không thuộc về bạn");
        }
    }

    // 6. Cancel discount Code [User/Student]
    //    @Transactional
    //    public void cancelDiscountCode(String userEmail, String discountCode) {
    //        Discount discount = discountRepository.findByDiscountCode(discountCode)
    //                .orElseThrow(() -> new InvalidDiscountCodeException("Mã giảm giá không tồn tại"));
    //
    //        // Kiểm tra xem user đã sử dụng mã này chưa
    //        if (!discountUsageRepository.existsByUserEmailAndDiscount(userEmail, discount)) {
    //            throw new InvalidDiscountCodeException("Bạn chưa sử dụng mã giảm giá này");
    //        }
    //
    //        // Xóa lịch sử sử dụng mã giảm giá của user
    //        discountUsageRepository.deleteByUserEmailAndDiscount(userEmail, discount);
    //
    //        // Giảm số lần sử dụng của mã giảm giá
    //        discount.setUsesCount(discount.getUsesCount() - 1);
    //        discountRepository.save(discount);
    //    }
}
