package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing roles.
 */
@RestController
@RequestMapping("/api/v1/")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody Role role) {
        Role newRole = roleService.createRole(role);
        ApiResponse<Role> response = wrapSuccessResponse("Role created successfully", newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/roles/{name}")
    public ResponseEntity<ApiResponse<Role>> getRole(@PathVariable String name) {
        Role role = roleService.getRole(name);
        ApiResponse<Role> response = wrapSuccessResponse("Role retrieved successfully", role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<PagedResponse<Role>>> getAllRoles(
            @PageableDefault(size = 5, page = 0, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<Role> roles = roleService.getAllRoles(pageable);
        ApiResponse<PagedResponse<Role>> response = wrapSuccessResponse("Roles retrieved successfully", roles);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/roles")
    public ResponseEntity<ApiResponse<Role>> updateRole(@RequestBody Role role) {
        Role updatedRole = roleService.updateRole(role);
        ApiResponse<Role> response = wrapSuccessResponse("Role updated successfully", updatedRole);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/roles/{name}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String name) {
        roleService.deleteRole(name);
        ApiResponse<Void> response = wrapSuccessResponse("Role deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
