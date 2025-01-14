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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<String> addDiscountVoucher(@Valid @RequestBody DiscountCreateReq discountCreateReq) {
        Long instructorId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        String discountCode = discountService.addDiscount(discountCreateReq, instructorId);
        return successRes("Discount created successfully", discountCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<PagedRes<DiscountGetRes>> getDiscountsForInstructor(
            @PageableDefault(size = 5, page = 0, sort = "discountName", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        Long instructorId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<DiscountGetRes> discounts = discountService.getDiscountsForInstructor(pageable, instructorId);
        return successRes("Retrieved successfully", discounts);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/valid")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Boolean> validateDiscountForCourse(@RequestBody ApplyDiscountReq request) {
        var isAccept = discountService.validateDiscountForCourse(request.getDiscountCode(), request.getCourseId());
        if (!isAccept) {
            throw new InvalidDiscountException("Mã giảm giá không áp dụng cho khóa học này");
        }
        return successRes("Retrieved successfully", true);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isDiscountOwner(#id))")
    public ApiRes<Void> delDiscountVoucher(@PathVariable Long id) {
        discountService.delDiscountVoucher(id);
        return successRes("Deleted successfully", null);
    }
}
