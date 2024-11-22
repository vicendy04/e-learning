package com.myproject.elearning.service;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.request.discount.DiscountCreateRequest;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.repository.DiscountRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final UserService userService;
    private final CourseService courseService;
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;

    // set role instructor cho controller
// sửa lại is_active, instructor_id của DiscountCreateRequest
// logic code existst phải kết hợp với instructor_id
// dùng token để lấy email của instructor
    // 1. Generator Discount Code [Shop/Instructor]
    @Transactional
    public String generateDiscountCode(DiscountCreateRequest request) {
        Long id = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        userRepository.getReferenceById(id);

        // Kiểm tra xem mã giảm giá đã tồn tại chưa
        if (discountRepository.existsByInstructorIdAndDiscountCode(id, request.getDiscountCode())) {
            throw new DuplicateDiscountCodeException("Mã giảm giá đã tồn tại");
        }

        Discount discount = new Discount();
        discount.setDiscountName(request.getDiscountName());
        discount.setDiscountDescription(request.getDiscountDescription());
        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setDiscountCode(request.getDiscountCode());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setMaxUses(request.getMaxUses());
        discount.setMaxUsesPerUser(request.getMaxUsesPerUser());
        discount.setMinOrderValue(request.getMinOrderValue());
        discount.setAppliesTo(request.getAppliesTo());
        discount.setInstructor(instructor);

        if (request.getAppliesTo() == Discount.DiscountAppliesTo.SPECIFIC) {
            discount.setSpecificCourseIds(new HashSet<>(request.getSpecificCourseIds()));
        }

        return discountMapper.toDto(discountRepository.save(discount));
    }

    // xem lai --> getAllDiscountCodesByInstructor ben duoi
    // 2. Get all discount codes [User/Student | Shop/Instructor] 
//    public PagedResponse<DiscountResponse> getAllDiscountCodes(String email, Pageable pageable) {
//        User user = userService.getUser(email);
//        Page<Discount> discounts;
//
//        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.INSTRUCTOR)) {
//            // Nếu là instructor, lấy các mã giảm giá do họ tạo
//            discounts = discountRepository.findAllByInstructor(user, pageable);
//        } else {
//            // Nếu là student, lấy tất cả mã giảm giá đang active
//            discounts = discountRepository.findAllByIsActiveTrue(pageable);
//        }
//
//        return PagedResponse.from(discounts.map(discountMapper::toDto));
//    }

//    public PagedResponse<DiscountResponse> getAllDiscountCodesByInstructor(String instructorId) {
//        User user = userService.getUser(instructorId);
//        Page<Discount> discounts;
//
//        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.INSTRUCTOR)) {
//            // Nếu là instructor, lấy các mã giảm giá do họ tạo
//            discounts = discountRepository.findAllByInstructor(user, pageable);
//        }
//
//        return PagedResponse.from(discounts.map(discountMapper::toDto));
//    }

    //  làm phân trang, xem lại new ArrayList, sửa tên
    // 3. Get all product by discount code [User/Student]
//    public List<CourseResponse> getAllProductsByDiscountCode(String discountCode) {
//        Discount discount = discountRepository.findByDiscountCode(discountCode)
//                .orElseThrow(() -> new InvalidDiscountCodeException("Mã giảm giá không tồn tại"));
//
//        if (!discount.getIsActive()) {
//            throw new InvalidDiscountCodeException("Mã giảm giá đã hết hạn hoặc không còn hiệu lực");
//        }
//
//        if (discount.getAppliesTo() == Discount.DiscountAppliesTo.ALL) {
//            // Lấy tất cả khóa học của instructor
//            return courseService.getAllCoursesByInstructor(discount.getInstructor().getId());
//        } else {
//            // Lấy các khóa học cụ thể
//            return courseService.getAllCoursesByIds(new ArrayList<>(discount.getSpecificCourseIds()));
//        }
//    }

    // dùng instructor_id để check xem có phải khóa học của họ không thay vì check trong course
    // 4. Get discount amount [User/Student]
//    public BigDecimal getDiscountAmount(String discountCode, Long courseId, BigDecimal originalPrice) {
//        Discount discount = discountRepository.findByDiscountCode(discountCode)
//                .orElseThrow(() -> new InvalidDiscountCodeException("Mã giảm giá không tồn tại"));
//
//        Course course = courseService.getCourseEntity(courseId);
//
//        if (!discount.isApplicableToCourse(course)) {
//            throw new InvalidDiscountCodeException("Mã giảm giá không áp dụng cho khóa học này");
//        }
//// thêm vào hàm isApplicableToCourse luôn
//        if (originalPrice.compareTo(discount.getMinOrderValue()) < 0) {
//            throw new InvalidDiscountCodeException("Giá trị đơn hàng chưa đạt mức tối thiểu");
//        }
//
//        if (discount.getDiscountType() == Discount.DiscountType.FIXED_AMOUNT) {
//            return discount.getDiscountValue();
//        } else {
//            return originalPrice.multiply(discount.getDiscountValue().divide(new BigDecimal("100")));
//        }
//    }

    // dùng id
    // 5. Delete discount Code [Shop/Instructor]
//    @Transactional
//    public void deleteDiscountCode(String instructorEmail, String discountCode) {
//        User instructor = userService.getUser(instructorEmail);
//        Discount discount = discountRepository.findByDiscountCodeAndInstructor(discountCode, instructor)
//                .orElseThrow(() -> new InvalidDiscountCodeException("Mã giảm giá không tồn tại hoặc không thuộc về bạn"));
//
//        discountRepository.delete(discount);
//    }

    // thêm trường discount_users_used thay vì tạo bản riêng
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