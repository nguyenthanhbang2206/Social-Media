package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.FriendShipResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.mapper.FriendShipMapper;
import com.nguyenthanhbang.Social_media.mapper.UserMapper;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.service.FriendShipService;
import com.nguyenthanhbang.Social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FriendShipController {
    private final FriendShipService friendShipService;
    private final FriendShipMapper friendShipMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping("/friend-requests/{userId}")
    public ResponseEntity<ApiResponse<FriendShipResponse>> sendFriendRequest(@PathVariable Long userId) {
        FriendShip friendShip = friendShipService.sendRequest(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Send request successfully")
                .status(HttpStatus.CREATED.value())
                .data(friendShipMapper.toFriendShipResponse(friendShip))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @DeleteMapping("/friend-requests/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long userId) {
        friendShipService.cancelRequest(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete request successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/friend-requests/{userId}/accept")
    public ResponseEntity<ApiResponse<FriendShipResponse>> acceptFriend(@PathVariable Long userId) {
        FriendShip friendShip = friendShipService.acceptFriend(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Accept friend successfully")
                .status(HttpStatus.OK.value())
                .data(friendShipMapper.toFriendShipResponse(friendShip))
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/friend-requests/{userId}/refuse")
    public ResponseEntity<ApiResponse<Void>> refuseFriend(@PathVariable Long userId) {
        friendShipService.refuseFriend(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Refuse friend successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/friends/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(@PathVariable Long userId) {
        friendShipService.unfriend(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete friend successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/friends/{userId}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFriends(@PathVariable Long userId) {
        List<User> users = friendShipService.getFriends(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Get friends successfully")
                .status(HttpStatus.OK.value())
                .data(userMapper.toUserResponses(users))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/friend-requests/received")
    public ResponseEntity<ApiResponse<List<FriendShipResponse>>> getRequestReceived() {
        List<FriendShip> friendShips = friendShipService.getFriendRequestsReceived();
        ApiResponse response = ApiResponse.builder()
                .message("Get friends request received successfully")
                .status(HttpStatus.OK.value())
                .data(friendShipMapper.toFriendShipResponses(friendShips))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/friends/status/{userId}")
    public ResponseEntity<ApiResponse<FriendShipResponse>> getFriendStatus(@PathVariable Long userId) {
        User user = userService.getUserLogin();
        FriendShip friendShip = friendShipService.findFriendshipBetween(user.getId(), userId);
        ApiResponse response = ApiResponse.builder()
                .message("Get friend status successfully")
                .status(HttpStatus.OK.value())
                .data(friendShipMapper.toFriendShipResponse(friendShip))
                .build();
        return ResponseEntity.ok(response);
    }

}
