package com.myproject.elearning.service;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.dto.UserDTO;
import com.myproject.elearning.service.mapper.UserMapper;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
// @Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow();
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setUsername(userDTO.getUsername());
        user.setImageUrl(userDTO.getImageUrl());
        userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.usersToUserDTOs(users);
    }
}
