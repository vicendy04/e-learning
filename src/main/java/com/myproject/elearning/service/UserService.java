package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.UserMapper.USER_MAPPER;

import com.myproject.elearning.constant.AuthConstants;
import com.myproject.elearning.domain.RefreshToken;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.dto.request.auth.RegisterReq;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateReq;
import com.myproject.elearning.dto.response.user.UserRes;
import com.myproject.elearning.exception.problemdetails.InvalidCredentialsEx;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.RoleRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.repository.specification.UserSpec;
import com.myproject.elearning.security.SecurityUtils;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Transactional
    public UserRes addUser(RegisterReq registerReq) {
        String encryptedPassword = passwordEncoder.encode(registerReq.getPassword());
        registerReq.setPassword(encryptedPassword);
        User user = USER_MAPPER.toEntity(registerReq);
        Set<Role> roles = new HashSet<>();
        if (registerReq.isInstructor()) {
            roles.add(roleRepository.getReferenceById(3L));
        } else {
            roles.add(roleRepository.getReferenceById(2L));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return USER_MAPPER.toRes(user);
    }

    @Transactional(readOnly = true)
    public UserRes getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidIdEx(id));
        return USER_MAPPER.toRes(user);
    }

    @Transactional
    public void overrideRefreshToken(String id, String newRefreshToken, Instant expirationDate) {
        Long userId = Long.parseLong(id);
        RefreshToken refreshToken = refreshTokenRepository
                .findByUserIdAndDeviceName(userId, "A")
                .orElseGet(() -> this.createNewRefreshToken(userId));
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiryDate(expirationDate);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public UserAuth findAuthDTOByEmail(String email) {
        return userRepository
                .findAuthDTOByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsEx("Invalid email or user does not exist"));
    }

    public String findEmailById(Long id) {
        return userRepository.findEmailById(id).orElseThrow(() -> new InvalidIdEx("Email not found!"));
    }

    private RefreshToken createNewRefreshToken(Long userId) {
        User userOnlyId = userRepository.getReferenceById(userId);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userOnlyId);
        refreshToken.setDeviceName("A"); // hardcode
        return refreshToken;
    }

    @Transactional
    public UserRes editUser(Long id, UserUpdateReq userUpdateReq) {
        User user =
                userRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Entity with ID \" + id + \" not found"));
        USER_MAPPER.partialUpdate(user, userUpdateReq);
        return USER_MAPPER.toRes(user);
    }

    public void delUser(Long id) {
        userRepository.deleteById(id);
    }

    public PagedRes<UserRes> getUsers(UserSearchDTO searchDTO, Pageable pageable) {
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthConstants.ADMIN)) {
            throw new AccessDeniedException("Chỉ admin mới có quyền xem danh sách người dùng");
        }

        Specification<User> spec = UserSpec.filterUsers(searchDTO);
        Page<UserRes> users = userRepository.findAll(spec, pageable).map(USER_MAPPER::toRes);
        return PagedRes.from(users);
    }
}
