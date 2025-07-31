package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.UpdateGroupMemberRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.GroupMemberResponse;
import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.mapper.GroupMemberMapper;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class GroupMemberController {
    private final GroupMemberService groupMemberService;
    private final GroupMemberMapper groupMemberMapper;

    @PostMapping("/groups/{groupId}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(@PathVariable Long groupId) {
        groupMemberService.joinGroup(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Join group successfully")
                .status(HttpStatus.CREATED.value())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/groups/{groupId}/left")
    public ResponseEntity<ApiResponse<Void>> leftGroup(@PathVariable Long groupId) {
        groupMemberService.leaveGroup(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Left group successfully")
                .status(HttpStatus.CREATED.value())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/groups/{groupId}/members/{userId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupMemberService.approveMember(groupId, userId);
        ApiResponse response = ApiResponse.builder()
                .message("Approve member successfully")
                .status(HttpStatus.CREATED.value())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupMemberService.deleteMember(groupId, userId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete member successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/groups/{groupId}/members")
    public ResponseEntity<ApiResponse<List<GroupMember>>> getMembers(@PathVariable Long groupId) {
        List<GroupMember> groupMembers = groupMemberService.getMembers(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Get members successfully")
                .status(HttpStatus.OK.value())
                .data(groupMemberMapper.toGroupMemberResponses(groupMembers))
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/groups/{groupId}/members/{userId}/role")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> changeRole(@PathVariable Long groupId, @PathVariable Long userId, @RequestBody UpdateGroupMemberRequest request) {
        GroupMember groupMember = groupMemberService.changeRole(groupId, userId, request.getRole());
        ApiResponse response = ApiResponse.builder()
                .message("Change successfully")
                .status(HttpStatus.OK.value())
                .data(groupMemberMapper.toGroupMemberResponse(groupMember))
                .build();
        return ResponseEntity.ok(response);
    }


}
