package com.myproject.elearning.rest.auth;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;
import static com.myproject.elearning.security.SecurityUtils.getCurrentUserId;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.user.RegInstructorReq;
import com.myproject.elearning.dto.response.user.RegInstructorRes;
import com.myproject.elearning.service.InstructorService;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
@RestController
public class InstructorController {
    InstructorService instructorService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register-teacher")
    @PreAuthorize("isAuthenticated() and (hasRole('USER'))")
    public ApiRes<RegInstructorRes> registerTeacher(
            @RequestPart("request") RegInstructorReq request, @RequestPart("cv") MultipartFile cv) throws IOException {
        Long userId = getCurrentUserId();
        var res = instructorService.registerTeacher(request, cv, userId);
        return successRes("User created successfully", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Void> approveTeacherRegistration(@PathVariable Long userId) {
        instructorService.approveTeacherRegistration(userId);
        return successRes("Teacher registration approved successfully", null);
    }
}
