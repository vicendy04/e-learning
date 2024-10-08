package com.myproject.elearning.service;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.dto.UserDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
// @Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return UserDTO.from(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserDTO::from).collect(Collectors.toList());
    }
}
