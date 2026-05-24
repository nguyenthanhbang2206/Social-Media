package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.PostShareRequest;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostShareResponse;
import com.nguyenthanhbang.Social_media.mapper.PostShareMapper;
import com.nguyenthanhbang.Social_media.model.PostShare;
import com.nguyenthanhbang.Social_media.service.PostShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PostShareController {
    private final PostShareService postShareService;
    private final PostShareMapper postShareMapper;

    @PostMapping("/posts/{postId}/shares")
    public ResponseEntity<ApiResponse<PostShareResponse>> sharePost(@PathVariable Long postId,
                                                                    @RequestBody PostShareRequest request) {
        PostShare postShare = postShareService.sharePost(postId, request);
        ApiResponse response = ApiResponse.builder()
                .message("Share post successfully")
                .status(HttpStatus.CREATED.value())
                .data(postShareMapper.toPostShareResponse(postShare))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}/shares")
    public ResponseEntity<ApiResponse<List<PostShareResponse>>> getShares(@PathVariable Long postId) {
        List<PostShare> postShares = postShareService.getSharesByPost(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Get shares successfully")
                .status(HttpStatus.OK.value())
                .data(postShareMapper.toPostShareResponses(postShares))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}/shares/{shareId}")
    public ResponseEntity<ApiResponse<Void>> deleteShare(@PathVariable Long postId, @PathVariable Long shareId) {
        postShareService.deleteShare(postId, shareId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete share successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

