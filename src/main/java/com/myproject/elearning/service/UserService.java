package com.myproject.elearning.service;

import com.myproject.elearning.domain.RefreshToken;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.auth.RegisterRequest;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateRequest;
import com.myproject.elearning.dto.response.user.UserGetResponse;
import com.myproject.elearning.exception.problemdetails.EmailAlreadyUsedException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.user.UserGetMapper;
import com.myproject.elearning.mapper.user.UserRegisterMapper;
import com.myproject.elearning.mapper.user.UserUpdateMapper;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.RoleRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.repository.specification.UserSpecification;
import com.myproject.elearning.security.AuthoritiesConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserGetMapper userGetMapper;
    private final UserRegisterMapper userRegisterMapper;
    private final UserUpdateMapper userUpdateMapper;

    public UserGetResponse createUser(RegisterRequest registerRequest) {
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(encryptedPassword);
        User user = userRegisterMapper.toEntity(registerRequest);
        Set<Role> roles = new HashSet<>();
        if (userRepository.count() == 0) {
            roles.add(roleRepository.getReferenceById(AuthoritiesConstants.ADMIN));
            roles.add(roleRepository.getReferenceById(AuthoritiesConstants.USER));
        } else {
            roles.add(roleRepository.getReferenceById(AuthoritiesConstants.USER));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return userGetMapper.toDto(user);
    }

    public UserGetResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        return userGetMapper.toDto(user);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EmailAlreadyUsedException(email));
    }

    @Transactional
    public void overrideRefreshToken(String id, String newRefreshToken, Instant expirationDate) {
        Long userId = Long.parseLong(id);
        RefreshToken refreshToken = refreshTokenRepository
                .findByUserIdAndDeviceName(userId, "A")
                .orElseGet(() -> this.createNewRefreshToken(userId)); // or override
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(expirationDate);
        refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken createNewRefreshToken(Long userId) {
        User userOnlyId = userRepository.getReferenceById(userId);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userOnlyId);
        refreshToken.setDeviceName("A"); // hardcode
        return refreshToken;
    }

    public UserGetResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        userUpdateMapper.partialUpdate(user, userUpdateRequest);
        userRepository.save(user);
        return userGetMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        userRepository.delete(user);
    }

    public PagedResponse<UserGetResponse> getAllUsers(UserSearchDTO searchDTO, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(searchDTO);
        Page<UserGetResponse> users = userRepository.findAll(spec, pageable).map(userGetMapper::toDto);
        return PagedResponse.from(users);
    }
}
