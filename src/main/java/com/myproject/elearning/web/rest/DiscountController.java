package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.discount.ApplyDiscountReq;
import com.myproject.elearning.dto.request.discount.DiscountCreateReq;
import com.myproject.elearning.dto.response.discount.DiscountGetRes;
import com.myproject.elearning.exception.problemdetails.InvalidDiscountException;
import com.myproject.elearning.service.DiscountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing discount.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/discounts")
@RestController
public class DiscountController {
    DiscountService discountService;

    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @PostMapping("")
    public ResponseEntity<?> addDiscountVoucher(@Valid @RequestBody DiscountCreateReq discountCreateReq) {
        String discountCode = discountService.addDiscount(discountCreateReq);
        ApiRes<String> response = successRes("Discount created successfully", discountCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    public ResponseEntity<?> getDiscountsForInstructor(
            @PageableDefault(size = 5, page = 0, sort = "discountName", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        PagedRes<DiscountGetRes> discounts = discountService.getDiscountsForInstructor(pageable);
        ApiRes<?> response = successRes("Retrieved successfully", discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/valid")
    public ResponseEntity<?> validateDiscountForCourse(@RequestBody ApplyDiscountReq request) {
        var isAccept = discountService.validateDiscountForCourse(request.getDiscountCode(), request.getCourseId());
        if (!isAccept) {
            throw new InvalidDiscountException("Mã giảm giá không áp dụng cho khóa học này");
        }
        ApiRes<?> response = successRes("Retrieved successfully", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delDiscountVoucher(@PathVariable Long id) {
        discountService.delDiscountVoucher(id);
        ApiRes<?> response = successRes("Deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
