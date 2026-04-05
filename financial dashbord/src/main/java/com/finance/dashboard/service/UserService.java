package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.UpdateRoleRequest;
import com.finance.dashboard.dto.request.UpdateStatusRequest;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    public UserResponse updateRole(Long id, UpdateRoleRequest request) {
        User user = findOrThrow(id);
        user.setRole(request.getRole());
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse updateStatus(Long id, UpdateStatusRequest request) {
        User user = findOrThrow(id);
        user.setActive(request.getIsActive());
        return UserResponse.from(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = findOrThrow(id);
        userRepository.delete(user);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
