package com.myproject.elearning.service;

import com.myproject.elearning.domain.RefreshToken;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.auth.RegisterReq;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import com.myproject.elearning.dto.request.user.UserUpdateReq;
import com.myproject.elearning.dto.response.user.UserGetRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.UserMapper;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.RoleRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.repository.specification.UserSpecification;
import com.myproject.elearning.security.AuthoritiesConstants;
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
    UserMapper userMapper;

    @Transactional
    public UserGetRes addUser(RegisterReq registerReq) {
        String encryptedPassword = passwordEncoder.encode(registerReq.getPassword());
        registerReq.setPassword(encryptedPassword);
        User user = userMapper.toEntity(registerReq);
        Set<Role> roles = new HashSet<>();
        if (userRepository.count() == 0) {
            roles.add(roleRepository.getReferenceById(1L));
            roles.add(roleRepository.getReferenceById(3L));
        } else {
            roles.add(roleRepository.getReferenceById(2L));
        }
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toGetResponse(user);
    }

    public UserGetRes getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        return userMapper.toGetResponse(user);
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

    @Transactional
    public UserGetRes editUser(Long id, UserUpdateReq userUpdateReq) {
        Long currentUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        if (!currentUserId.equals(id) && SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)) {
            throw new AccessDeniedException("Bạn không có quyền sửa thông tin người dùng này");
        }

        User user = userRepository.getReferenceIfExists(id);
        userMapper.partialUpdate(user, userUpdateReq);
        User savedUser = userRepository.save(user);
        return userMapper.toGetResponse(savedUser);
    }

    public void delUser(Long id) {
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)) {
            throw new AccessDeniedException("Chỉ admin mới có quyền xóa người dùng");
        }

        if (!userRepository.existsById(id)) {
            throw new InvalidIdException(id);
        }
        userRepository.deleteByUserId(id);
    }

    public PagedRes<UserGetRes> getUsers(UserSearchDTO searchDTO, Pageable pageable) {
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)) {
            throw new AccessDeniedException("Chỉ admin mới có quyền xem danh sách người dùng");
        }

        Specification<User> spec = UserSpecification.filterUsers(searchDTO);
        Page<UserGetRes> users = userRepository.findAll(spec, pageable).map(userMapper::toGetResponse);
        return PagedRes.from(users);
    }
}
