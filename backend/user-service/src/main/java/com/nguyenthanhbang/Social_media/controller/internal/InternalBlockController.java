package com.nguyenthanhbang.Social_media.controller.internal;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/blocks")
@RequiredArgsConstructor
public class InternalBlockController {
    private final BlockRepository blockRepository;

    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> exists(@RequestParam Long userId, @RequestParam Long targetId) {
        boolean blocked = blockRepository.existsBlockBetween(userId, targetId);
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Check block successfully")
                .data(blocked)
                .build();
        return ResponseEntity.ok(response);
    }
}
