package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.RoleMapper.ROLE_MAPPER;

import com.myproject.elearning.constant.ErrorMessageConstants;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.role.CreateRoleReq;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
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

    public RoleDTO addRole(CreateRoleReq req) {
        Role role = new Role();
        role.setName(req.getName());
        Role saved = roleRepository.save(role);
        return ROLE_MAPPER.toDto(saved);
    }

    public RoleDTO getRole(Long id) {
        Role role = roleRepository
                .findById(id)
                .orElseThrow(() -> new InvalidIdEx(ErrorMessageConstants.ROLE_NOT_FOUND + id));
        return ROLE_MAPPER.toDto(role);
    }

    public RoleDTO editRole(RoleDTO roleDTO) {
        Role role = roleRepository
                .findById(roleDTO.getId())
                .orElseThrow(() -> new InvalidIdEx(ErrorMessageConstants.ROLE_NOT_FOUND + roleDTO.getId()));
        role.setName(roleDTO.getName());
        Role savedRole = roleRepository.save(role);
        return ROLE_MAPPER.toDto(savedRole);
    }

    public void delRole(Long id) {
        roleRepository.deleteById(id);
    }

    public PagedRes<RoleDTO> getRoles(Pageable pageable) {
        Page<Role> rolesPage = roleRepository.findAll(pageable);
        return PagedRes.from(rolesPage.map(ROLE_MAPPER::toDto));
    }
}
