package com.myproject.elearning.rest.auth;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.auth.RegisterReq;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateReq;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.user.UserRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.EnrollService;
import com.myproject.elearning.service.UserService;
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
 * REST controller for managing users
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    UserService userService;
    EnrollService enrollService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ApiRes<UserRes> addUser(@Valid @RequestBody RegisterReq registerReq) {
        UserRes newUser = userService.addUser(registerReq);
        return successRes("User created successfully", newUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<UserRes> getUser(@PathVariable(name = "userId") Long userId) {
        UserRes user = userService.getUser(userId);
        return successRes("User retrieved successfully", user);
    }

    // Danger: @PostAuthorize should not be used as it only checks after method execution, which could lead to unwanted
    // data changes
    // @PostAuthorize("hasAnyRole('ADMIN') or returnObject.data.user.id == #jwt.subject")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isEnrollmentOwner(#enrollmentId))")
    public ApiRes<EnrollmentGetRes> getEnrollment(@PathVariable Long enrollmentId) {
        EnrollmentGetRes enrollment = enrollService.getEnrollment(enrollmentId);
        return successRes("Enrollment retrieved successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<PagedRes<UserRes>> getUsers(
            @ModelAttribute UserSearchDTO searchDTO,
            @PageableDefault(size = 5, page = 0, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<UserRes> users = userService.getUsers(searchDTO, pageable);
        return successRes("Users retrieved successfully", users);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isUserOwner(#id))")
    public ApiRes<UserRes> editUser(
            @PathVariable(name = "userId") Long userId, @RequestBody UserUpdateReq userUpdateReq) {
        UserRes updatedUser = userService.editUser(userId, userUpdateReq);
        return successRes("User updated successfully", updatedUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrollments")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER', 'ADMIN')")
    public ApiRes<PagedRes<EnrollmentGetRes>> getMyEnrollments(@PageableDefault(size = 5, page = 0) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        PagedRes<EnrollmentGetRes> enrollments = enrollService.getMyEnrollments(pageable, curUserId);
        return successRes("User enrollments retrieved successfully", enrollments);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Void> delUser(@PathVariable(name = "userId") Long userId) {
        userService.delUser(userId);
        return successRes("User deleted successfully", null);
    }
}
