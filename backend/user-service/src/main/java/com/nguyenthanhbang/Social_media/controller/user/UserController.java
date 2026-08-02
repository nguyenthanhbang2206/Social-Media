package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.UpdateUserRequest;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.mapper.UserMapper;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * VẤN ĐỀ TÌM THẤY: UserController gốc thiếu endpoint GET /users/email/{email}.
 *
 * 4 service khác (group-service, interaction-service, post-service,
 * notification-service) đều cần gọi sang user-service để resolve user
 * theo EMAIL (không phải id) – đây là pattern bắt buộc vì mỗi service
 * chỉ nhận được X-User-Email từ Gateway (Keycloak JWT email claim),
 * không có sẵn Long id local của user-service.
 *
 * Method getUserByEmail(String) đã có sẵn trong UserService interface và
 * UserServiceImpl (xem service/impl/UserServiceImpl.java), nhưng KHÔNG
 * được expose qua bất kỳ REST endpoint nào trong bản gốc → Feign client
 * ở các service khác sẽ luôn nhận lỗi 404 nếu cố gọi.
 *
 * Đã thêm @GetMapping("/users/email/{email}") bên dưới, dùng đúng
 * UserMapper hiện có để giữ format response nhất quán với các endpoint
 * khác trong cùng file.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        List<User> users = userService.getActiveUsers();
        ApiResponse response = ApiResponse.builder()
                .message("Get users successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponses(users))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Get user by id successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponse(user))
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * MỚI: endpoint còn thiếu trong bản gốc, cần thiết để UserClient
     * (Feign) ở các service khác gọi userClient.getUserByEmail(email)
     * hoạt động được. Không có endpoint này, request sẽ trả 404 và toàn
     * bộ flow tạo post/comment/group-member sẽ fail khi cần resolve user
     * hiện tại theo email.
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable("email") String email) {
        User user = userService.getUserByEmail(email);
        ApiResponse response = ApiResponse.builder()
                .message("Get user by email successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponse(user))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        User user = userService.getUserLogin();
        ApiResponse response = ApiResponse.builder()
                .message("Get profile successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponse(user))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestBody UpdateUserRequest request) {
        User user = userService.updateProfile(request);
        ApiResponse response = ApiResponse.builder()
                .message("Update profile successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponse(user))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam("keyword") String keyword) {
        List<User> users = userService.searchUser(keyword);
        ApiResponse response = ApiResponse.builder()
                .message("Search users successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponses(users))
                .build();
        return ResponseEntity.ok(response);
    }
}
