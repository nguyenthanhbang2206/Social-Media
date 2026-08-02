package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.model.Block;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.BlockRepository;
import com.nguyenthanhbang.Social_media.service.BlockService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {
    private final BlockRepository blockRepository;
    private final UserService userService;

    @Override
    public Block blockUser(Long blockedUserId, String reason) {
        User blocker = userService.getUserLogin();
        if (blocker.getId().equals(blockedUserId)) {
            throw new IllegalArgumentException("You can not block yourself");
        }
        userService.getUserById(blockedUserId);
        Block block = blockRepository.findByBlockerIdAndBlockedId(blocker.getId(), blockedUserId)
                .orElseGet(Block::new);
        block.setBlockerId(blocker.getId());
        block.setBlockedId(blockedUserId);
        block.setReason(reason);
        return blockRepository.save(block);
    }

    @Override
    public void unblockUser(Long blockedUserId) {
        User blocker = userService.getUserLogin();
        Block block = blockRepository.findByBlockerIdAndBlockedId(blocker.getId(), blockedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Block not found"));
        blockRepository.delete(block);
    }

    @Override
    public List<Block> getMyBlocks() {
        User blocker = userService.getUserLogin();
        return blockRepository.findByBlockerId(blocker.getId());
    }

    @Override
    public void ensureNotBlocked(Long targetUserId) {
        User currentUser = userService.getUserLogin();
        if (blockRepository.existsBlockBetween(currentUser.getId(), targetUserId)) {
            throw new IllegalStateException("Blocked user");
        }
    }

    @Override
    public boolean existsBlockBetween(Long userId, Long targetId) {
        return blockRepository.existsBlockBetween(userId, targetId);
    }
}
