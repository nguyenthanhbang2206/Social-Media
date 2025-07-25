package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.enumeration.FriendShipStatus;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import com.nguyenthanhbang.Social_media.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    Optional<FriendShip> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<FriendShip> findByReceiverIdAndStatus(Long receiverId, FriendShipStatus status);

    @Query("SELECT f.sender FROM FriendShip f WHERE f.receiver.id = :id and f.sender.active = true and  f.receiver.active = true and f.status = 'ACCEPTED'")
    List<User> findFriendIsSender(@Param("id") Long id);
    @Query("SELECT f.receiver FROM FriendShip f WHERE f.sender.id = :id and f.receiver.active = true and  f.sender.active = true and f.status = 'ACCEPTED'")
    List<User> findFriendIsReceiver(@Param("id") Long id);
}
