package com.nguyenthanhbang.Social_media.controller.admin;

import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminUserController {
    private final UserService userService;
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByStatus(@RequestParam(required = false) Boolean active){
        List<User> users = userService.getAllUsers(active);
        ApiResponse response = ApiResponse.builder()
                .message("Get users successfully")
                .status(HttpStatus.OK.value())
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(@PathVariable Long id){
        User user = userService.changeStatus(id);
        ApiResponse response = ApiResponse.builder()
                .message("Change status successfully")
                .status(HttpStatus.OK.value())
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

}
