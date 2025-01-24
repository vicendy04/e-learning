package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.UserMapper.USER_MAPPER;

import com.myproject.elearning.constant.RegistrationStatus;
import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.user.RegInstructorReq;
import com.myproject.elearning.dto.response.user.RegInstructorRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.exception.problemdetails.EmailUsedEx;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.RoleRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class InstructorService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    FileService fileService;

    @Transactional
    public RegInstructorRes registerTeacher(RegInstructorReq request, MultipartFile cv) throws IOException {
        Long userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        User userCurrent = userRepository.findById(userId).orElseThrow(() -> new InvalidIdEx(userId));
        if (userCurrent.getRegistrationStatus() == null
                || userCurrent.getRegistrationStatus().equals(RegistrationStatus.REJECTED)) {
            String cvFileName = fileService.store(cv, "upload");
            request.setCvUrl("/upload/" + cvFileName);
            USER_MAPPER.partialUpdate(userCurrent, request);
            userCurrent.setRegistrationStatus(RegistrationStatus.PENDING);
            userRepository.save(userCurrent);
            return USER_MAPPER.toRegTeacherRes(userCurrent);
        }
        throw new EmailUsedEx("Your request is pending review");
    }

    @Transactional
    public void approveTeacherRegistration(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidIdEx(userId));
        if (user.getRegistrationStatus() != RegistrationStatus.PENDING) {
            throw new EmailUsedEx("Registration is not pending");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.getReferenceById(3L));
        user.setRoles(roles);
        user.setRegistrationStatus(RegistrationStatus.APPROVED);
        userRepository.save(user);
    }
}
