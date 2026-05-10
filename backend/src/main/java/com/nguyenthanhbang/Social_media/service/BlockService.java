package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.model.Block;

import java.util.List;

public interface BlockService {
    Block blockUser(Long blockedUserId, String reason);
    void unblockUser(Long blockedUserId);
    List<Block> getMyBlocks();
    void ensureNotBlocked(Long targetUserId);
}

