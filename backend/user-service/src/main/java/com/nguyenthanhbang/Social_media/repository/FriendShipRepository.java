package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.common.enumeration.FriendShipStatus;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    Optional<FriendShip> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<FriendShip> findByReceiverIdAndStatus(Long receiverId, FriendShipStatus status);

    List<FriendShip> findBySenderIdAndStatus(Long senderId, FriendShipStatus status);
}
