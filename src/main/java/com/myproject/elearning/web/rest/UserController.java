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
import org.springframework.http.ResponseEntity;
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

    @PostMapping("")
    public ResponseEntity<ApiRes<UserGetRes>> addUser(@Valid @RequestBody RegisterReq registerReq) {
        UserGetRes newUser = userService.addUser(registerReq);
        ApiRes<UserGetRes> response = successRes("User created successfully", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiRes<UserGetRes>> getUser(@PathVariable(name = "id") Long id) {
        UserGetRes user = userService.getUser(id);
        ApiRes<UserGetRes> response = successRes("User retrieved successfully", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiRes<PagedRes<UserGetRes>>> getUsers(
            @ModelAttribute UserSearchDTO searchDTO,
            @PageableDefault(size = 5, page = 0, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {

        PagedRes<UserGetRes> users = userService.getUsers(searchDTO, pageable);
        ApiRes<PagedRes<UserGetRes>> response = successRes("Users retrieved successfully", users);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<UserGetRes>> editUser(
            @PathVariable(name = "id") Long id, @RequestBody UserUpdateReq userUpdateReq) {
        UserGetRes updatedUser = userService.editUser(id, userUpdateReq);
        ApiRes<UserGetRes> response = successRes("User updated successfully", updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> delUser(@PathVariable(name = "id") Long id) {
        userService.delUser(id);
        ApiRes<Void> response = successRes("User deleted successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
