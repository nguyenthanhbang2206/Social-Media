package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.common.enumeration.FriendShipStatus;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.FriendShipRepository;
import com.nguyenthanhbang.Social_media.repository.UserRepository;
import com.nguyenthanhbang.Social_media.service.FriendShipService;
import com.nguyenthanhbang.Social_media.service.UserService;
import com.nguyenthanhbang.Social_media.service.BlockService;
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
    private final BlockService blockService;

    @Override
    public FriendShip sendRequest(Long userId) {
        blockService.ensureNotBlocked(userId);
        User currentUser = userService.getUserLogin();
        userService.getUserById(userId);
        FriendShip friendShip = new FriendShip();
        friendShip.setSenderId(currentUser.getId());
        friendShip.setReceiverId(userId);
        friendShip.setStatus(FriendShipStatus.PENDING);
//        currentUser.getSentFriendRequests().add(friendShip);
//        user.getReceivedFriendRequests().add(friendShip);
        return friendShipRepository.save(friendShip);
    }

    @Override
    public void cancelRequest(Long userId) {
        blockService.ensureNotBlocked(userId);
        User currentUser = userService.getUserLogin();
        userService.getUserById(userId);
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(currentUser.getId(), userId).orElseThrow(()->new EntityNotFoundException("Not found"));
        friendShipRepository.delete(friendShip);
    }

    @Override
    public FriendShip acceptFriend(Long userId) {
        blockService.ensureNotBlocked(userId);
        User currentUser = userService.getUserLogin();
        userService.getUserById(userId);
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(userId, currentUser.getId()).orElseThrow(()->new EntityNotFoundException("Not found"));
        friendShip.setStatus(FriendShipStatus.ACCEPTED);
        friendShip.setAcceptedAt(LocalDateTime.now());
        return friendShipRepository.save(friendShip);
    }

    @Override
    public void refuseFriend(Long userId) {
        blockService.ensureNotBlocked(userId);
        User currentUser = userService.getUserLogin();
        userService.getUserById(userId);
        FriendShip friendShip = friendShipRepository.findBySenderIdAndReceiverId(userId, currentUser.getId()).orElseThrow(()->new EntityNotFoundException("Not found"));
        friendShipRepository.delete(friendShip);
    }

    @Override
    public void unfriend(Long userId) {
        blockService.ensureNotBlocked(userId);
        User currentUser = userService.getUserLogin();
        userService.getUserById(userId);
        FriendShip friendShip = this.findFriendshipBetween(currentUser.getId(), userId);
        friendShipRepository.delete(friendShip);
    }

    @Override
    public List<User> getFriends(Long userId) {
        userService.getUserById(userId);
        List<FriendShip> sent = friendShipRepository.findBySenderIdAndStatus(userId, FriendShipStatus.ACCEPTED);
        List<FriendShip> received = friendShipRepository.findByReceiverIdAndStatus(userId, FriendShipStatus.ACCEPTED);
        List<Long> friendIds = new java.util.ArrayList<>(sent.stream().map(FriendShip::getReceiverId).toList());
        friendIds.addAll(received.stream().map(FriendShip::getSenderId).toList());
        return userRepository.findAllById(friendIds);
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
