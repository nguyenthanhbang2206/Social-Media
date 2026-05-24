package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.BlockRequest;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.BlockResponse;
import com.nguyenthanhbang.Social_media.mapper.BlockMapper;
import com.nguyenthanhbang.Social_media.model.Block;
import com.nguyenthanhbang.Social_media.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/blocks")
public class BlockController {
    private final BlockService blockService;
    private final BlockMapper blockMapper;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<BlockResponse>> blockUser(@PathVariable Long userId,
                                                                @RequestBody(required = false) BlockRequest request) {
        String reason = request == null ? null : request.getReason();
        Block block = blockService.blockUser(userId, reason);
        ApiResponse response = ApiResponse.builder()
                .message("Block user successfully")
                .status(HttpStatus.CREATED.value())
                .data(blockMapper.toBlockResponse(block))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable Long userId) {
        blockService.unblockUser(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Unblock user successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getMyBlocks() {
        List<Block> blocks = blockService.getMyBlocks();
        ApiResponse response = ApiResponse.builder()
                .message("Get blocks successfully")
                .status(HttpStatus.OK.value())
                .data(blockMapper.toBlockResponses(blocks))
                .build();
        return ResponseEntity.ok(response);
    }
}

