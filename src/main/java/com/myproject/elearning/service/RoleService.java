package com.myproject.elearning.service;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing roles.
 */
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRole(String name) {
        return roleRepository
                .findById(name)
                .orElseThrow(() -> new InvalidIdException("Role not found with name: " + name));
    }

    public Role updateRole(Role role) {
        Role currentRole = roleRepository
                .findById(role.getName())
                .orElseThrow(() -> new InvalidIdException("Role not found with name: " + role.getName()));
        currentRole.setName(role.getName());
        return roleRepository.save(currentRole);
    }

    public void deleteRole(String name) {
        Role role = roleRepository
                .findById(name)
                .orElseThrow(() -> new InvalidIdException("Role not found with name: " + name));
        roleRepository.delete(role);
    }

    public PagedResponse<Role> getAllRoles(Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(pageable);
        return PagedResponse.from(roles);
    }
}
