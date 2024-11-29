package com.myproject.elearning.service;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.exception.constants.ErrorMessageConstants;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.RoleMapper;
import com.myproject.elearning.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing roles.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleDTO addRole(RoleDTO role) {
        Role save = roleRepository.save(roleMapper.toEntity(role));
        return roleMapper.toDto(save);
    }

    public RoleDTO getRole(String name) {
        Role role = roleRepository
                .findById(name)
                .orElseThrow(() -> new InvalidIdException(ErrorMessageConstants.ROLE_NOT_FOUND + name));
        return roleMapper.toDto(role);
    }

    public RoleDTO editRole(RoleDTO roleDTO) {
        Role role = roleRepository
                .findById(roleDTO.getName())
                .orElseThrow(() -> new InvalidIdException(ErrorMessageConstants.ROLE_NOT_FOUND + roleDTO.getName()));
        role.setName(roleDTO.getName());
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    public void delRole(String name) {
        Role role = roleRepository
                .findById(name)
                .orElseThrow(() -> new InvalidIdException(ErrorMessageConstants.ROLE_NOT_FOUND + name));
        roleRepository.delete(role);
    }

    public PagedRes<RoleDTO> getRoles(Pageable pageable) {
        Page<Role> rolesPage = roleRepository.findAll(pageable);
        return PagedRes.from(rolesPage.map(roleMapper::toDto));
    }
}
