package com.myproject.elearning.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final ProductService productService;

    @PostMapping("/lifecycle")
    public void demonstrateLifecycle() {
        productService.demonstrateLifecycle();
    }
}
