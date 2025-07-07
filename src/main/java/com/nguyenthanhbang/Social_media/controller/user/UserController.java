package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.UpdateUserRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsers(){
        List<User> users = userService.getActiveUsers();
        ApiResponse response = ApiResponse.builder()
                .message("Get users successfully")
                .status(HttpStatus.OK.value())
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Get user by id successfully")
                .status(HttpStatus.OK.value())
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(){
        User user = userService.getUserLogin();
        ApiResponse response = ApiResponse.builder()
                .message("Get profile successfully")
                .status(HttpStatus.OK.value())
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/users/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(@RequestBody UpdateUserRequest request){
        User user = userService.updateProfile(request);
        ApiResponse response = ApiResponse.builder()
                .message("Update profile successfully")
                .status(HttpStatus.OK.value())
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }
}
