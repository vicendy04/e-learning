package com.myproject.elearning.web.rest;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.content.ContentCreateRequest;
import com.myproject.elearning.dto.request.content.ContentUpdateRequest;
import com.myproject.elearning.dto.request.discount.DiscountCreateRequest;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.service.ContentService;
import com.myproject.elearning.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

/**
 * REST controller for managing discount.
 */
@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;
    @GetMapping("")
    public ResponseEntity<?> getContent() {
        var res = discountService.generateDiscountCode("123", new DiscountCreateRequest());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
