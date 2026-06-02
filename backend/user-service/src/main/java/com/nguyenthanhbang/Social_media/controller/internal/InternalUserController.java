package com.nguyenthanhbang.Social_media.controller.internal;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.mapper.UserMapper;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.service.BlockService;
import com.nguyenthanhbang.Social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/internal")
public class InternalUserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final BlockService blockService;

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") Long id) throws InterruptedException {
        User user = userService.getUserById(id);
//        Thread.sleep(5000);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .message("Get user by id successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponse(user))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/blocks/exists")
    public ResponseEntity<ApiResponse<Boolean>> exists(@RequestParam("userId") Long userId,
                                                       @RequestParam("targetId") Long targetId) {
        boolean blocked = blockService.existsBlockBetween(userId, targetId);
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Check block successfully")
                .data(blocked)
                .build();
        return ResponseEntity.ok(response);
    }
}

