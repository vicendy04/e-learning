package com.myproject.elearning.service;

import com.myproject.elearning.domain.RefreshToken;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.mapper.UserMapper;
import com.myproject.elearning.dto.request.RegisterRequest;
import com.myproject.elearning.dto.request.UserRequest;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.dto.response.UserResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.RoleRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.AuthoritiesConstants;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
// @Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(RegisterRequest registerRequest) {
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(encryptedPassword);
        User user = userMapper.registerRequestToUser(registerRequest);
        Set<Role> roles = new HashSet<>();

        if (userRepository.count() == 0) {
            roles.add(roleRepository
                    .findById(AuthoritiesConstants.ADMIN)
                    .orElseThrow(() -> new InvalidIdException("Role not found")));
            roles.add(roleRepository
                    .findById(AuthoritiesConstants.USER)
                    .orElseThrow(() -> new InvalidIdException("Role not found")));
        } else {
            roles.add(roleRepository
                    .findById(AuthoritiesConstants.USER)
                    .orElseThrow(() -> new InvalidIdException("Role not found")));
        }

        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
    }

    @Transactional
    public void updateUserWithRefreshToken(String email, String newRefreshToken, Instant expirationDate) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidIdException("Email not found!"));
        RefreshToken refreshToken = user.getRefreshToken().stream()
                .filter(rt -> "A".equals(rt.getDeviceName())) // hardcode
                .findFirst()
                .orElse(null);
        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            user.getRefreshToken().add(refreshToken);
        }
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(expirationDate);
        userRepository.save(user);
    }

    public UserResponse updateUser(UserRequest userRequest) {
        User user = userRepository
                .findById(userRequest.getId())
                .orElseThrow(() -> new InvalidIdException(userRequest.getId()));
        user.setEmail(userRequest.getEmail().toLowerCase());
        user.setUsername(userRequest.getUsername());
        user.setImageUrl(userRequest.getImageUrl());
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        userRepository.delete(user);
    }

    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userRepository.findAll(pageable).map(UserResponse::from);
        return PagedResponse.from(users);
    }
}
