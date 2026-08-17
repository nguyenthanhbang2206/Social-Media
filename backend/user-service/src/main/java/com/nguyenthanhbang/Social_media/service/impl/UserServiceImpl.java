package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.common.enumeration.Role;
import com.nguyenthanhbang.Social_media.common.event.UserUpdateEvent;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.CreateUserRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdateUserRequest;
import com.nguyenthanhbang.Social_media.event.UserUpdatePublisher;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.UserRepository;
import com.nguyenthanhbang.Social_media.service.KeycloakService;
import com.nguyenthanhbang.Social_media.service.UserService;
import com.nguyenthanhbang.Social_media.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final UserUpdatePublisher userUpdatePublisher;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret:}")
    private String clientSecret;

    @Override
    public User createUser(CreateUserRequest request) {
        User currentUser = userRepository.findByEmailAndActiveTrue(request.getEmail());
        if (currentUser != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        currentUser = new User();
        currentUser.setActive(true);
        currentUser.setEmail(request.getEmail());
        currentUser.setFullName(request.getFullName());
        currentUser.setRole(Role.USER);
        currentUser.setPassword("KEYCLOAK_MANAGED");
        return userRepository.save(currentUser);
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User getUserLogin() {
        String email = RequestHeaderUtil.getUserEmail()
                .orElseThrow(() -> {
                    log.error("X-User-Email header is missing or empty");
                    return new EntityNotFoundException("User not found - X-User-Email header missing");
                });
        log.info("Getting user by email: {}", email);
        return this.getUserByEmail(email);
    }

    @Override
    public User updateProfile(UpdateUserRequest request) {
        User user = this.getUserLogin();
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        user = userRepository.save(user);

        userUpdatePublisher.publishUserUpdatedEvent(UserUpdateEvent.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .active(user.getActive())
                .build());

        return user;
    }

    @Override
    public List<User> getAllUsers(Boolean active) {
        if (active == null) {
            return userRepository.findAll();
        } else if (active == true) {
            return userRepository.findByActiveTrue();
        } else {
            return userRepository.findByActiveFalse();
        }
    }

    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    @Override
    public User changeStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setActive(!user.getActive());
        user = userRepository.save(user);

        userUpdatePublisher.publishUserUpdatedEvent(UserUpdateEvent.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .active(user.getActive())
                .build());

        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<User> searchUser(String keyword) {
        return userRepository.search(keyword);
    }

  
    @Override
    @Transactional
    public User syncUserFromKeycloak(CreateUserRequest request) {
        // Idempotent: đã tồn tại thì trả về luôn (nhưng liên kết keycloakId nếu chưa có)
        User existing = userRepository.findByEmailAndActiveTrue(request.getEmail());
        if (existing != null) {
            if (request.getKeycloakUserId() != null && !request.getKeycloakUserId().isBlank()) {
                try {
                    UUID keycloakId = UUID.fromString(request.getKeycloakUserId());
                    if (existing.getKeycloakId() == null || !existing.getKeycloakId().equals(keycloakId)) {
                        log.info("syncUserFromKeycloak: Binding existing user email={} to keycloakId={}", request.getEmail(), keycloakId);
                        existing.setKeycloakId(keycloakId);
                        existing = userRepository.save(existing);
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("syncUserFromKeycloak: invalid keycloakUserId='{}'", request.getKeycloakUserId());
                }
            }
            log.info("syncUserFromKeycloak: user already exists, skipping. email={}", request.getEmail());
            return existing;
        }

        // Resolve username không trùng
        String baseUsername = (request.getUsername() != null && !request.getUsername().isBlank())
                ? request.getUsername()
                : request.getEmail().split("@")[0];
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + "_" + suffix++;
        }

        User user = new User();
        user.setActive(true);
        user.setEmail(request.getEmail());
        user.setUsername(username);
        user.setFullName(request.getFullName() != null ? request.getFullName() : username);
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setEnabled(true);
        user.setEmailVerified(false);

        // Gán keycloakId nếu có (field tên keycloakUserId trong CreateUserRequest)
        if (request.getKeycloakUserId() != null && !request.getKeycloakUserId().isBlank()) {
            try {
                user.setKeycloakId(UUID.fromString(request.getKeycloakUserId()));
            } catch (IllegalArgumentException e) {
                log.warn("syncUserFromKeycloak: invalid keycloakUserId='{}', skipping", request.getKeycloakUserId());
            }
        }

        user = userRepository.save(user);
        log.info("syncUserFromKeycloak: created local user email={}, id={}", user.getEmail(), user.getId());
        userUpdatePublisher.publishUserUpdatedEvent(UserUpdateEvent.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .active(user.getActive())
                .build());

        return user;
    }
}
