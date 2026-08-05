package com.nguyenthanhbang.Social_media.common.outbox.repository;

import com.nguyenthanhbang.Social_media.common.outbox.model.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    @Query(value = "SELECT * FROM outbox_messages WHERE status = :status " +
           "ORDER BY created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED", 
           nativeQuery = true)
    List<OutboxMessage> findMessagesForProcessing(
            @Param("status") String status,
            @Param("limit") int limit
    );
}
