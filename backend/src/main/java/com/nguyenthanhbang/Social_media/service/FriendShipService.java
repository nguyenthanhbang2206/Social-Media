package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.response.FriendShipResponse;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import com.nguyenthanhbang.Social_media.model.User;

import java.util.List;

public interface FriendShipService {
    FriendShip sendRequest(Long userId);
    void cancelRequest(Long userId);
    FriendShip acceptFriend(Long userId);
    void refuseFriend(Long userId);
    void unfriend(Long userId);
    List<User> getFriends(Long userId);
    List<FriendShip> getFriendRequestsReceived();
    FriendShip findFriendshipBetween(Long userId1, Long userId2);
}
