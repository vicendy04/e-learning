package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.auth.RegisterReq;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateReq;
import com.myproject.elearning.dto.response.user.UserGetRes;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ApiRes<UserGetRes> addUser(@Valid @RequestBody RegisterReq registerReq) {
        UserGetRes newUser = userService.addUser(registerReq);
        return successRes("User created successfully", newUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<UserGetRes> getUser(@PathVariable(name = "id") Long id) {
        UserGetRes user = userService.getUser(id);
        return successRes("User retrieved successfully", user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<PagedRes<UserGetRes>> getUsers(
            @ModelAttribute UserSearchDTO searchDTO,
            @PageableDefault(size = 5, page = 0, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<UserGetRes> users = userService.getUsers(searchDTO, pageable);
        return successRes("Users retrieved successfully", users);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isUserOwner(#id))")
    public ApiRes<UserGetRes> editUser(@PathVariable(name = "id") Long id, @RequestBody UserUpdateReq userUpdateReq) {
        UserGetRes updatedUser = userService.editUser(id, userUpdateReq);
        return successRes("User updated successfully", updatedUser);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Void> delUser(@PathVariable(name = "id") Long id) {
        userService.delUser(id);
        return successRes("User deleted successfully", null);
    }
}
