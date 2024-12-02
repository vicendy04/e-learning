package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing roles.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/roles")
@RestController
public class RoleController {
    RoleService roleService;

    @PostMapping("")
    public ResponseEntity<ApiRes<RoleDTO>> addRole(@Valid @RequestBody RoleDTO role) {
        RoleDTO newRole = roleService.addRole(role);
        ApiRes<RoleDTO> response = successRes("Role created successfully", newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<RoleDTO>> getRole(@PathVariable Long id) {
        RoleDTO role = roleService.getRole(id);
        ApiRes<RoleDTO> response = successRes("Role retrieved successfully", role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiRes<PagedRes<RoleDTO>>> getRoles(
            @PageableDefault(size = 5, page = 0, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<RoleDTO> roles = roleService.getRoles(pageable);
        ApiRes<PagedRes<RoleDTO>> response = successRes("Roles retrieved successfully", roles);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("")
    public ResponseEntity<ApiRes<RoleDTO>> editRole(@RequestBody RoleDTO role) {
        RoleDTO updatedRole = roleService.editRole(role);
        ApiRes<RoleDTO> response = successRes("Role updated successfully", updatedRole);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> delRole(@PathVariable Long id) {
        roleService.delRole(id);
        ApiRes<Void> response = successRes("Role deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
