package com.myproject.elearning.web.rest;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

/**
 * REST controller for managing roles.
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@Valid @RequestBody RoleDTO role) {
        RoleDTO newRole = roleService.createRole(role);
        ApiResponse<RoleDTO> response = wrapSuccessResponse("Role created successfully", newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRole(@PathVariable String name) {
        RoleDTO role = roleService.getRole(name);
        ApiResponse<RoleDTO> response = wrapSuccessResponse("Role retrieved successfully", role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<RoleDTO>>> getAllRoles(
            @PageableDefault(size = 5, page = 0, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<RoleDTO> roles = roleService.getAllRoles(pageable);
        ApiResponse<PagedResponse<RoleDTO>> response = wrapSuccessResponse("Roles retrieved successfully", roles);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(@RequestBody RoleDTO role) {
        RoleDTO updatedRole = roleService.updateRole(role);
        ApiResponse<RoleDTO> response = wrapSuccessResponse("Role updated successfully", updatedRole);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String name) {
        roleService.deleteRole(name);
        ApiResponse<Void> response = wrapSuccessResponse("Role deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
