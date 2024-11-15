package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.auth.RegisterRequest;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateRequest;
import com.myproject.elearning.dto.response.user.UserGetResponse;
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
    public ResponseEntity<ApiResponse<UserGetResponse>> createUser(
            @Valid @RequestBody RegisterRequest registerRequest) {
        UserGetResponse newUser = userService.createUser(registerRequest);
        ApiResponse<UserGetResponse> response = wrapSuccessResponse("User created successfully", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserGetResponse>> getUser(@PathVariable(name = "id") Long id) {
        UserGetResponse user = userService.getUser(id);
        ApiResponse<UserGetResponse> response = wrapSuccessResponse("User retrieved successfully", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<UserGetResponse>>> getAllUsers(
            @ModelAttribute UserSearchDTO searchDTO,
            @PageableDefault(size = 5, page = 0, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {

        PagedResponse<UserGetResponse> users = userService.getAllUsers(searchDTO, pageable);
        ApiResponse<PagedResponse<UserGetResponse>> response =
                wrapSuccessResponse("Users retrieved successfully", users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserGetResponse>> updateUser(
            @PathVariable(name = "id") Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        UserGetResponse updatedUser = userService.updateUser(id, userUpdateRequest);
        ApiResponse<UserGetResponse> response = wrapSuccessResponse("User updated successfully", updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = wrapSuccessResponse("User deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
