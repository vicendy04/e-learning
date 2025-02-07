package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;
import static com.myproject.elearning.security.SecurityUtils.getCurrentUserId;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.discount.ApplyDiscountReq;
import com.myproject.elearning.dto.request.discount.DiscountCreateReq;
import com.myproject.elearning.dto.response.discount.DiscountRes;
import com.myproject.elearning.exception.problemdetails.InvalidDiscountEx;
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
        Long instructorId = getCurrentUserId();
        String discountCode = discountService.addDiscount(discountCreateReq, instructorId);
        return successRes("Discount created successfully", discountCode);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<PagedRes<DiscountRes>> getDiscountsForInstructor(
            @PageableDefault(size = 5, page = 0, sort = "discountName", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        Long instructorId = getCurrentUserId();
        var discounts = discountService.getDiscountsForInstructor(instructorId, pageable);
        return successRes("Retrieved successfully", discounts);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/consume")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Boolean> consumeDiscount(@RequestBody ApplyDiscountReq request) {
        var isAccept = discountService.consumeDiscount(request.getDiscountCode(), request.getCourseId());
        if (!isAccept) {
            throw new InvalidDiscountEx("Mã giảm giá không thể áp dụng");
        }
        return successRes("Retrieved successfully", true);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{discountId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isDiscountOwner(#discountId))")
    public ApiRes<Void> delDiscountVoucher(@PathVariable Long discountId) {
        discountService.delDiscountVoucher(discountId);
        return successRes("Deleted successfully", null);
    }
}
