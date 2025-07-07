package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.CreateUserRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdateUserRequest;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.mapper.UserMapper;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.UserRepository;
import com.nguyenthanhbang.Social_media.service.UserService;
import com.nguyenthanhbang.Social_media.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void updateTokenOfUser(String email, String refreshToken) {
        User user = userRepository.findByEmailAndActiveTrue(email);
        if(user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    @Override
    public User createUser(CreateUserRequest request) {
        User currentUser = userRepository.findByEmailAndActiveTrue(request.getEmail());
        if(currentUser != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        currentUser = new User();
        currentUser.setActive(true);
        currentUser.setEmail(request.getEmail());
        currentUser.setFullName(request.getFullName());
        currentUser.setRole(request.getRole());
        currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(currentUser);
    }


    @Override
    public User getUserByRefreshTokenAndEmail(String refreshToken, String email)  {
        User user = userRepository.findByRefreshTokenAndEmailAndActiveTrue(refreshToken, email);
        if(user == null) {
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User getUserLogin() {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user = this.getUserByEmail(email);
        return user;
    }

    @Override
    public User updateProfile(UpdateUserRequest request) {
        User user = this.getUserLogin();
        user.setFullName(request.getFullName());
        // user.setAvatar(request.getAvatar());
        user.setGender(request.getGender());
        user = userRepository.save(user);
        return user;
    }

    @Override
    public List<User> getAllUsers(Boolean active) {
        if(active == null){
            return userRepository.findAll();
        }
        else if(active == true) {
            return userRepository.findByActiveTrue();
        }else{
            return userRepository.findByActiveFalse();
        }
    }

    @Override
    public List<UserResponse> getActiveUsers() {
        List<User> users = userRepository.findByActiveTrue();
        return userMapper.toUserResponses(users);
    }

    @Override
    public User changeStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setActive(!user.getActive());
        user = userRepository.save(user);
        return user;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }


}