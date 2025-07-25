package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.response.FriendShipResponse;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.enumeration.FriendShipStatus;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.FriendShipRepository;
import com.nguyenthanhbang.Social_media.repository.UserRepository;
import com.nguyenthanhbang.Social_media.service.FriendShipService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendShipServiceImpl implements FriendShipService {
    private final UserService userService;
    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;

    @Override
    public FriendShip sendRequest(Long userId) {
        User currentUser = userService.getUserLogin();
        User user = userService.getUserById(userId);
        FriendShip friendShip = new FriendShip();
        friendShip.setSender(currentUser);
        friendShip.setReceiver(user);
        friendShip.setStatus(FriendShipStatus.PENDING);
//        currentUser.getSentFriendRequests().add(friendShip);
//        user.getReceivedFriendRequests().add(friendShip);
        return friendShipRepository.save(friendShip);
    }

    @Override
    public void cancelRequest(Long userId) {
        User currentUser = userService.getUserLogin();
        User user = userService.getUserById(userId);
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(currentUser.getId(), userId).orElseThrow(()->new EntityNotFoundException("Not found"));
        friendShipRepository.delete(friendShip);
    }

    @Override
    public FriendShip acceptFriend(Long userId) {
        User currentUser = userService.getUserLogin();
        User user = userService.getUserById(userId);
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(userId, currentUser.getId()).orElseThrow(()->new EntityNotFoundException("Not found"));
        friendShip.setStatus(FriendShipStatus.ACCEPTED);
        friendShip.setAcceptedAt(LocalDateTime.now());
        return friendShipRepository.save(friendShip);
    }

    @Override
    public FriendShip refuseFriend(Long userId) {
        User currentUser = userService.getUserLogin();
        User user = userService.getUserById(userId);
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(userId, currentUser.getId()).orElseThrow(()->new EntityNotFoundException("Not found"));
        friendShip.setStatus(FriendShipStatus.DECLINED);
        return friendShipRepository.save(friendShip);
    }

    @Override
    public void unfriend(Long userId) {
        User currentUser = userService.getUserLogin();
        User user = userService.getUserById(userId);
        FriendShip friendShip = this.findFriendshipBetween(currentUser.getId(), user.getId());
        friendShipRepository.delete(friendShip);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = userService.getUserById(userId);
        List<User> friends = friendShipRepository.findFriendIsSender(user.getId());
        friends.addAll(friendShipRepository.findFriendIsReceiver(user.getId()));
        return friends;
    }

    @Override
    public List<FriendShip> getFriendRequestsReceived() {
        User user = userService.getUserLogin();
        return friendShipRepository.findByReceiverIdAndStatus(user.getId(), FriendShipStatus.PENDING);
    }

    @Override
    public FriendShip findFriendshipBetween(Long userId1, Long userId2) {
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(userId1, userId2).or(() -> friendShipRepository.findBySenderIdAndReceiverId(userId2, userId1)).orElse(null);
        return friendShip;
    }


}
