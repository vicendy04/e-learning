package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.discount.ApplyDiscountReq;
import com.myproject.elearning.dto.request.discount.DiscountCreateReq;
import com.myproject.elearning.dto.response.discount.DiscountGetRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidDiscountException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.DiscountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing discount.
 */
@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountController {
    DiscountService discountService;

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiRes<String> addDiscountVoucher(@Valid @RequestBody DiscountCreateReq discountCreateReq) {
        Long instructorId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        String discountCode = discountService.addDiscount(discountCreateReq, instructorId);
        return successRes("Discount created successfully", discountCode);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<DiscountGetRes>> getDiscountsForInstructor(
            @PageableDefault(size = 5, page = 0, sort = "discountName", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        Long instructorId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<DiscountGetRes> discounts = discountService.getDiscountsForInstructor(pageable, instructorId);
        return successRes("Retrieved successfully", discounts);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/valid")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<Boolean> validateDiscountForCourse(@RequestBody ApplyDiscountReq request) {
        var isAccept = discountService.validateDiscountForCourse(request.getDiscountCode(), request.getCourseId());
        if (!isAccept) {
            throw new InvalidDiscountException("Mã giảm giá không áp dụng cho khóa học này");
        }
        return successRes("Retrieved successfully", true);
    }

    // Chỉ INSTRUCTOR (người tạo mã) và ADMIN mới được xóa
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> delDiscountVoucher(@PathVariable Long id) {
        discountService.delDiscountVoucher(id);
        return successRes("Deleted successfully", null);
    }
}
