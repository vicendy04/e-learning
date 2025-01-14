package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.role.CreateRoleReq;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<RoleDTO> addRole(@Valid @RequestBody CreateRoleReq req) {
        RoleDTO newRole = roleService.addRole(req);
        return successRes("Role created successfully", newRole);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<RoleDTO> getRole(@PathVariable Long id) {
        RoleDTO role = roleService.getRole(id);
        return successRes("Role retrieved successfully", role);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<PagedRes<RoleDTO>> getRoles(
            @PageableDefault(size = 5, page = 0, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<RoleDTO> roles = roleService.getRoles(pageable);
        return successRes("Roles retrieved successfully", roles);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<RoleDTO> editRole(@RequestBody RoleDTO role) {
        RoleDTO updatedRole = roleService.editRole(role);
        return successRes("Role updated successfully", updatedRole);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Void> delRole(@PathVariable Long id) {
        roleService.delRole(id);
        return successRes("Role deleted successfully", null);
    }
}
