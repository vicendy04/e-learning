package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.DiscountMapper.DISCOUNT_MAPPER;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.discount.DiscountCreateReq;
import com.myproject.elearning.dto.response.discount.DiscountRes;
import com.myproject.elearning.exception.problemdetails.InvalidDiscountEx;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.CourseRepository.CourseForValidDiscount;
import com.myproject.elearning.repository.DiscountRepository;
import java.time.LocalDateTime;
import java.util.Set;
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
public class DiscountService {
    DiscountRepository discountRepository;
    CourseRepository courseRepository;

    @Transactional
    public String addDiscount(DiscountCreateReq request, Long instructorId) {
        if (discountRepository.existsByInstructorIdAndDiscountCode(instructorId, request.getDiscountCode())) {
            throw new InvalidDiscountEx("Mã giảm giá đã tồn tại");
        }

        if (request.getAppliesTo() == Discount.DiscountAppliesTo.SPECIFIC) {
            Set<Long> instructorCourseIds = courseRepository.findIdsByInstructorId(instructorId);

            if (!instructorCourseIds.containsAll(request.getSpecificCourseIds())) {
                throw new InvalidDiscountEx("Một số khóa học không thuộc về bạn");
            }
        }

        Discount discount = DISCOUNT_MAPPER.toEntity(request, instructorId);
        discountRepository.save(discount);
        return request.getDiscountCode();
    }

    public PagedRes<DiscountRes> getDiscountsForInstructor(Long instructorId, Pageable pageable) {
        Page<DiscountRes> discounts =
                discountRepository.findAllByInstructorId(instructorId, pageable).map(DISCOUNT_MAPPER::toRes);
        return PagedRes.from(discounts);
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
                .orElseThrow(() -> new InvalidDiscountEx("Mã giảm giá không tồn tại"));
        CourseForValidDiscount course =
                courseRepository.findCourseDetailsForDiscount(courseId).orElseThrow(() -> new InvalidIdEx(courseId));
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
    public void delDiscountVoucher(Long discountId) {
        discountRepository.deleteById(discountId);
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
