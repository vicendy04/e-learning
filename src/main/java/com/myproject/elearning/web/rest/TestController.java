package com.myproject.elearning.web.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/test")
@RestController
public class TestController {
    @GetMapping("/{id}")
    public Object getUser(@PathVariable(name = "id") Long id) {
        System.out.println("test controller is running right now");
        return null;
    }
}
