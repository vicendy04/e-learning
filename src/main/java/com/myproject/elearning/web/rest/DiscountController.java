package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.discount.ApplyDiscountRequest;
import com.myproject.elearning.dto.request.discount.DiscountCreateRequest;
import com.myproject.elearning.dto.response.discount.DiscountGetResponse;
import com.myproject.elearning.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/discounts")
@RestController
public class DiscountController {
    private final DiscountService discountService;

    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @PostMapping("")
    public ResponseEntity<?> createDiscountVoucher(@Valid @RequestBody DiscountCreateRequest discountCreateRequest) {
        String discountCode = discountService.generateDiscountCode(discountCreateRequest);
        ApiResponse<String> response = wrapSuccessResponse("Discount created successfully", discountCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    public ResponseEntity<?> getDiscountsForInstructor(
            @PageableDefault(size = 5, page = 0, sort = "discountName", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        PagedResponse<DiscountGetResponse> discounts = discountService.getDiscountsForInstructor(pageable);
        ApiResponse<?> response = wrapSuccessResponse("Retrieved successfully", discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/valid")
    public ResponseEntity<?> validateDiscountForCourse(@RequestBody ApplyDiscountRequest request) {
        var discounts = discountService.validateDiscountForCourse(request.getDiscountCode(), request.getCourseId());
        ApiResponse<?> response = wrapSuccessResponse("Retrieved successfully", discounts);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @DeleteMapping("/{discountCode}")
    public ResponseEntity<?> deleteDiscountCode(@PathVariable String discountCode) {
        discountService.deleteDiscountCode(discountCode);
        ApiResponse<?> response = wrapSuccessResponse("Deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
