package com.myproject.elearning.service;

import com.myproject.elearning.domain.RefreshToken;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.dto.response.PagedResponse;
import com.myproject.elearning.service.dto.response.UserResponse;
import com.myproject.elearning.service.mapper.UserMapper;
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
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
    }

    public void updateUserWithRefreshToken(String email, String newRefreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidIdException("Email not found!"));
        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            user.setRefreshToken(refreshToken);
        }
        refreshToken.setToken(newRefreshToken);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public UserResponse updateUser(UserResponse userResponse) {
        User user = userRepository
                .findById(userResponse.getId())
                .orElseThrow(() -> new InvalidIdException(userResponse.getId()));
        user.setEmail(userResponse.getEmail().toLowerCase());
        user.setUsername(userResponse.getUsername());
        user.setImageUrl(userResponse.getImageUrl());
        userRepository.save(user);
        return userMapper.userToUserDTO(user);
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
