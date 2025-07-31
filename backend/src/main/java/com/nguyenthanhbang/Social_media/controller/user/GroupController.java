package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.GroupRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.GroupResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.mapper.GroupMapper;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.service.GroupService;
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

}
