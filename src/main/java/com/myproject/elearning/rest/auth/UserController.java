package com.myproject.elearning.rest.auth;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.auth.RegisterReq;
import com.myproject.elearning.dto.request.user.EditPreferencesReq;
import com.myproject.elearning.dto.request.user.SetPreferencesReq;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateReq;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.user.PreferenceRes;
import com.myproject.elearning.dto.response.user.UserRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.EnrollService;
import com.myproject.elearning.service.UserPreferenceService;
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
    UserPreferenceService userPreferenceService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ApiRes<UserRes> addUser(@Valid @RequestBody RegisterReq registerReq) {
        var newUser = userService.addUser(registerReq);
        return successRes("User created successfully", newUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<UserRes> getUser(@PathVariable(name = "userId") Long userId) {
        var user = userService.getUser(userId);
        return successRes("User retrieved successfully", user);
    }

    // Danger: @PostAuthorize should not be used as it only checks after method execution, which could lead to unwanted
    // data changes
    // @PostAuthorize("hasAnyRole('ADMIN') or returnObject.data.user.id == #jwt.subject")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isEnrollmentOwner(#enrollmentId))")
    public ApiRes<EnrollmentGetRes> getEnrollment(@PathVariable Long enrollmentId) {
        var enrollment = enrollService.getEnrollment(enrollmentId);
        return successRes("Enrollment retrieved successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<PagedRes<UserRes>> getUsers(
            @ModelAttribute UserSearchDTO searchDTO,
            @PageableDefault(size = 5, page = 0, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        var users = userService.getUsers(searchDTO, pageable);
        return successRes("Users retrieved successfully", users);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isUserOwner(#id))")
    public ApiRes<UserRes> editUser(
            @PathVariable(name = "userId") Long userId, @RequestBody UserUpdateReq userUpdateReq) {
        var editedUser = userService.editUser(userId, userUpdateReq);
        return successRes("User updated successfully", editedUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrollments")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER', 'ADMIN')")
    public ApiRes<PagedRes<EnrollmentGetRes>> getMyEnrollments(@PageableDefault(size = 5, page = 0) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        var enrollments = enrollService.getMyEnrollments(pageable, curUserId);
        return successRes("User enrollments retrieved successfully", enrollments);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Void> delUser(@PathVariable(name = "userId") Long userId) {
        userService.delUser(userId);
        return successRes("User deleted successfully", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/initial-preferences")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Void> setInitialPreferences(@Valid @RequestBody SetPreferencesReq request) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        userPreferenceService.setInitialPreferences(curUserId, request.getTopicIds());
        return successRes("Preferences set successfully", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<UserRes> updatePreferences(@Valid @RequestBody EditPreferencesReq request) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        userPreferenceService.updatePreferences(curUserId, request.getAddTopicIds(), request.getDelTopicIds());
        return successRes("Preferences updated successfully", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my-preferences")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER', 'ADMIN')")
    public ApiRes<PreferenceRes> getMyPreferences() {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        var res = userPreferenceService.getMyPreferences(curUserId);
        return successRes("User perferences retrieved successfully", res);
    }
}
