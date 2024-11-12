package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.RegisterInput;
import com.myproject.elearning.dto.request.UserUpdateInput;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.dto.response.UserResponse;
import com.myproject.elearning.service.UserService;
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
 * REST controller for managing users
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody RegisterInput registerInput) {
        UserResponse newUser = userService.createUser(registerInput);
        ApiResponse<UserResponse> response = wrapSuccessResponse("User created successfully", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable(name = "id") Long id) {
        User user = userService.getUser(id);
        ApiResponse<User> response = wrapSuccessResponse("User retrieved successfully", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @PageableDefault(size = 5, page = 0, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<UserResponse> users = userService.getAllUsers(pageable);
        ApiResponse<PagedResponse<UserResponse>> response = wrapSuccessResponse("Users retrieved successfully", users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserUpdateInput userUpdateInput) {
        UserResponse updatedUser = userService.updateUser(userUpdateInput);
        ApiResponse<UserResponse> response = wrapSuccessResponse("User updated successfully", updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = wrapSuccessResponse("User deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
