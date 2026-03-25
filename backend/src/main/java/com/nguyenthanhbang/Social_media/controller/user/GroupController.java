package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.GroupRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.GroupResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.mapper.GroupMapper;
import com.nguyenthanhbang.Social_media.mapper.PostMapper;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.service.GroupService;
import com.nguyenthanhbang.Social_media.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class GroupController {
    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping("/groups")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@RequestBody GroupRequest request){
        Group group = groupService.createGroup(request);
        ApiResponse response = ApiResponse.builder()
                .message("Creat group successfully")
                .status(HttpStatus.CREATED.value())
                .data(groupMapper.toGroupResponse(group))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> allGroups(){
        List<Group> groups = groupService.getAllGroups();
        ApiResponse response = ApiResponse.builder()
                .message("Get all groups successfully")
                .status(HttpStatus.OK.value())
                .data(groupMapper.toGroupResponses(groups))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/groups/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> groupDetails(@PathVariable Long id){
        Group group = groupService.getGroupById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Get group by id successfully")
                .status(HttpStatus.OK.value())
                .data(groupMapper.toGroupResponse(group))
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/groups/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(@PathVariable Long id, @RequestBody GroupRequest request){
        Group group = groupService.updateGroup(id, request);
        ApiResponse response = ApiResponse.builder()
                .message("Update group successfully")
                .status(HttpStatus.OK.value())
                .data(groupMapper.toGroupResponse(group))
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable Long id){
        groupService.deleteGroup(id);
        ApiResponse response = ApiResponse.builder()
                .message("Delete group successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/groups/search")
    public ResponseEntity<ApiResponse<List<Group>>> search(@RequestParam String keyword){
        List<Group> groups = groupService.searchGroup(keyword);
        ApiResponse response = ApiResponse.builder()
                .message("Search group successfully")
                .status(HttpStatus.OK.value())
                .data(groupMapper.toGroupResponses(groups))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/groups/my-group")
    public ResponseEntity<ApiResponse<List<Group>>> myGroup(){
        List<Group> groups = groupService.myGroups();
        ApiResponse response = ApiResponse.builder()
                .message("Get my groups successfully")
                .status(HttpStatus.OK.value())
                .data(groupMapper.toGroupResponses(groups))
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<PostResponse>> createPostGroup(@PathVariable Long groupId,@RequestBody CreatePostRequest request){
        Post post = postService.createPost(groupId, request);
        ApiResponse response = ApiResponse.builder()
                .message("Creat post group successfully")
                .status(HttpStatus.CREATED.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostApprovedForGroup(@PathVariable Long groupId){
        List<Post> posts = postService.getPostApprovedForGroup(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Get post approved group successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponses(posts))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/groups/{groupId}/posts/pending")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostPending(@PathVariable Long groupId){
        List<Post> posts = postService.getPostPending(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Get post pending successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponses(posts))
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/groups/{groupId}/posts/{postId}/approve")
    public ResponseEntity<ApiResponse<Void>> approvePost(@PathVariable Long groupId, @PathVariable Long postId){
        postService.approvePost(groupId, postId);
        ApiResponse response = ApiResponse.builder()
                .message("Approve post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

}
