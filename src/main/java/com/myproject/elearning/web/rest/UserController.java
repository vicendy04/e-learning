package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.service.UserService;
import com.myproject.elearning.service.dto.response.ApiResponse;
import com.myproject.elearning.service.dto.response.PagedResponse;
import com.myproject.elearning.service.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing users
 */
@RestController
@RequestMapping("/api/v1/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        ApiResponse<User> response = wrapSuccessResponse("User created successfully", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable(name = "id") Long id) {
        User user = userService.getUser(id);
        ApiResponse<User> response = wrapSuccessResponse("User retrieved successfully", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<UserResponse> users = userService.getAllUsers(pageable);
        ApiResponse<PagedResponse<UserResponse>> response = wrapSuccessResponse("Users retrieved successfully", users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserResponse userResponse) {
        UserResponse updatedUser = userService.updateUser(userResponse);
        ApiResponse<UserResponse> response = wrapSuccessResponse("User updated successfully", updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = wrapSuccessResponse("User deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
