package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.service.UserService;
import com.myproject.elearning.service.dto.ApiResponse;
import com.myproject.elearning.service.dto.UserDTO;
import jakarta.validation.Valid;
import java.util.List;
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
        ApiResponse<User> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("User created successfully");
        response.setData(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable(name = "id") Long id) {
        User user = userService.getUser(id);
        ApiResponse<User> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("User retrieved successfully");
        response.setData(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        ApiResponse<List<UserDTO>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Users retrieved successfully");
        response.setData(users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/users")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(userDTO);
        ApiResponse<UserDTO> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("User updated successfully");
        response.setData(updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("User deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
