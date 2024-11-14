package com.myproject.elearning.service;

import com.myproject.elearning.domain.RefreshToken;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.auth.RegisterRequest;
import com.myproject.elearning.dto.request.user.UserUpdateRequest;
import com.myproject.elearning.dto.response.user.UserGetResponse;
import com.myproject.elearning.exception.constants.ErrorMessageConstants;
import com.myproject.elearning.exception.problemdetails.EmailAlreadyUsedException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.user.UserGetMapper;
import com.myproject.elearning.mapper.user.UserRegisterMapper;
import com.myproject.elearning.mapper.user.UserUpdateMapper;
import com.myproject.elearning.repository.RoleRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.AuthoritiesConstants;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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
            roles.add(roleRepository
                    .findById(AuthoritiesConstants.ADMIN)
                    .orElseThrow(() -> new InvalidIdException(ErrorMessageConstants.ROLE_NOT_FOUND)));
            roles.add(roleRepository
                    .findById(AuthoritiesConstants.USER)
                    .orElseThrow(() -> new InvalidIdException(ErrorMessageConstants.ROLE_NOT_FOUND)));
        } else {
            roles.add(roleRepository
                    .findById(AuthoritiesConstants.USER)
                    .orElseThrow(() -> new InvalidIdException(ErrorMessageConstants.ROLE_NOT_FOUND)));
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

    public PagedResponse<UserGetResponse> getAllUsers(Pageable pageable) {
        Page<UserGetResponse> users = userRepository.findAll(pageable).map(userGetMapper::toDto);
        return PagedResponse.from(users);
    }
}
