package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.PostMediaRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.enumeration.*;
import com.nguyenthanhbang.Social_media.model.*;
import com.nguyenthanhbang.Social_media.repository.*;
import com.nguyenthanhbang.Social_media.service.GroupService;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMediaRepository postMediaRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostShareRepository postShareRepository;
    private final CommentRepository commentRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public Post createPost(Long groupId, CreatePostRequest request) {
        User user = userService.getUserLogin();
        Post post = new Post();
        post.setContent(request.getContent());
        PrivacyLevel privacy = request.getPrivacy() == null ? PrivacyLevel.PUBLIC : request.getPrivacy();
        post.setPrivacy(isUserPost(request.getPostType()) ? privacy : PrivacyLevel.PUBLIC);
        post.setUser(user);
        List<PostMedia> postMediaList = new ArrayList<>();
        List<PostMediaRequest> mediaRequests = request.getMedia() == null ? new ArrayList<>() : request.getMedia();
        for(PostMediaRequest item : mediaRequests) {
            PostMedia postMedia = PostMedia.builder()
                    .mediaUrl(item.getMediaUrl())
                    .mediaType(item.getMediaType())
                    .uploadOrder(item.getUploadOrder())
                    .post(post)
                    .build();
            postMediaList.add(postMedia);
        }
        post.setMedia(postMediaList);


        if(groupId != null && request.getPostType() == PostType.GROUP_POST) {
            Group group = groupService.getGroupById(groupId);
            GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).orElseThrow(()->new EntityNotFoundException("You have to join this group to create new post"));
            if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
                throw new EntityNotFoundException("You have to join this group to create new post");
            }
            post.setGroup(group);
            post.setPostType(PostType.GROUP_POST);
            if(group.getPrivacy() == GroupPrivacy.PRIVATE){
                if(groupMember.getRole() == GroupRole.ADMIN){
                    post.setIsApproved(true);
                }else {
                    post.setIsApproved(false);
                }
            }else {
                post.setIsApproved(true);
            }
        }else if(isUserPost(request.getPostType())){
            post.setPostType(PostType.USER_POST);
        }
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long groupId, Long postId, UpdatePostRequest request) {
        log.info("Update post");
        Post post = this.getPostById(postId);
        User user = userService.getUserLogin();
        if(user.getId() != post.getUser().getId()){
            throw new EntityNotFoundException("You do not have permission to update this post");
        }
        if (post.getMedia() == null) {
            post.setMedia(new ArrayList<>());
        }
        PrivacyLevel privacy = request.getPrivacy() == null ? PrivacyLevel.PUBLIC : request.getPrivacy();
        post.setPrivacy(isUserPost(request.getPostType()) ? privacy : PrivacyLevel.PUBLIC);
        post.setContent(request.getContent());
        List<PostMedia> postMediaList = post.getMedia(); // old
        Iterator<PostMedia> currentMediaIterator = postMediaList.iterator(); //old
        List<PostMediaRequest> mediaRequests = request.getMedia() == null ? new ArrayList<>() : request.getMedia();
        List<String> urls = mediaRequests.stream().map(item -> item.getMediaUrl()).collect(Collectors.toList()); //request
        while(currentMediaIterator.hasNext()){
            PostMedia item = currentMediaIterator.next();
            if(!urls.contains(item.getMediaUrl())){
                currentMediaIterator.remove();
            }
        }
        List<String> currentUrls = postMediaList.stream().map(item -> item.getMediaUrl()).collect(Collectors.toList()); // sau khi xóa
        for(PostMediaRequest item : mediaRequests){
            if(!currentUrls.contains(item.getMediaUrl())){
                PostMedia postMedia = PostMedia.builder()
                        .mediaType(item.getMediaType())
                        .mediaUrl(item.getMediaUrl())
                        .uploadOrder(item.getUploadOrder())
                        .post(post)
                        .build();
                postMediaList.add(postMedia);
            }
        }
        post.setMedia(postMediaList);

        if(groupId != null && request.getPostType() == PostType.GROUP_POST){
            Group group = groupService.getGroupById(groupId);
            GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).orElseThrow(()->new EntityNotFoundException("You have to join this group to create new post"));
            if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
                throw new EntityNotFoundException("You have to join this group to update this post");
            }

            if(group.getPrivacy() == GroupPrivacy.PRIVATE){
                if(groupMember.getRole() == GroupRole.ADMIN){
                    post.setIsApproved(true);
                }else {
                    post.setIsApproved(false);
                }
            }else{
                post.setIsApproved(true);
            }
        }else if(isUserPost(request.getPostType())){
            post.setPostType(PostType.USER_POST);
        }
        return postRepository.save(post);
    }

    @Override
    public List<Post> getPostApprovedForGroup(Long groupId) {
        Group group = groupService.getGroupById(groupId);
        User user = userService.getUserLogin();
        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId());
        if(groupMember.isPresent()){
            GroupMember exist = groupMember.get();
            if(group.getPrivacy() == GroupPrivacy.PRIVATE && exist.getStatus() != GroupMembershipStatus.APPROVED){
                throw new EntityNotFoundException("Wait for joining group to view group post");
            }
        }
        if(group.getPrivacy() == GroupPrivacy.PRIVATE && groupMember.isEmpty()){
            throw new EntityNotFoundException("Group is private. Join to view group post");
        }


        return postRepository.findByGroupIdAndIsApproved(groupId, true);
    }

    @Override
    public List<Post> getPostPending(Long groupId) {
        User user = userService.getUserLogin();
        Group group = groupService.getGroupById(groupId);
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).orElseThrow(()->new EntityNotFoundException("Group member not found"));
        if(groupMember.getRole() != GroupRole.ADMIN || groupMember.getStatus() != GroupMembershipStatus.APPROVED){
            throw new IllegalStateException("You can not view pending post");
        }
        return postRepository.findByGroupIdAndIsApproved(groupId, false);
    }

    @Override
    public void approvePost(Long groupId, Long postId) {
        Post post = postRepository.findByGroupIdAndId(groupId, postId).orElseThrow(()->new EntityNotFoundException("Post not found"));
        User user = userService.getUserLogin();
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).orElseThrow(()->new EntityNotFoundException("Group member not found"));
        if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
            throw new IllegalStateException("You can not approve post");
        }
        if(groupMember.getRole() != GroupRole.ADMIN){
            throw new IllegalStateException("You can not approve post");
        }
        if(post.getIsApproved() == true){
            throw new IllegalStateException("Post is already approved");
        }
        post.setIsApproved(true);
        postRepository.save(post);
    }

    @Override
    public void pinPost(Long groupId, Long postId) {
        Post post = postRepository.findByGroupIdAndId(groupId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userService.getUserLogin().getId())
                .orElseThrow(() -> new EntityNotFoundException("Group member not found"));
        if (groupMember.getStatus() != GroupMembershipStatus.APPROVED || groupMember.getRole() != GroupRole.ADMIN) {
            throw new IllegalStateException("You can not pin post");
        }
        post.setIsPinned(true);
        postRepository.save(post);
    }

    @Override
    public void unpinPost(Long groupId, Long postId) {
        Post post = postRepository.findByGroupIdAndId(groupId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userService.getUserLogin().getId())
                .orElseThrow(() -> new EntityNotFoundException("Group member not found"));
        if (groupMember.getStatus() != GroupMembershipStatus.APPROVED || groupMember.getRole() != GroupRole.ADMIN) {
            throw new IllegalStateException("You can not unpin post");
        }
        post.setIsPinned(false);
        postRepository.save(post);
    }

    @Override
    public List<Post> getNewsFeed() {
        List<Post> posts = postRepository.findAll();
        posts.forEach(this::populatePostTotals);
        return posts;
    }

    @Override
    public List<Post> getPostByUserId(Long userId) {
        User user = userService.getUserById(userId);
        List<Post> posts = postRepository.findByUserId(userId);
        posts.forEach(this::populatePostTotals);
        return posts;
    }

    @Override
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        populatePostTotals(post);
        return post;
    }

    @Override
    public void deletePost(Long postId) {
        Post post = this.getPostById(postId);
        post.setActive(false);
        postRepository.save(post);
        post.getMedia().forEach(item -> {
            item.setActive(false);
            postMediaRepository.save(item);
        });
    }

    private boolean isUserPost(PostType postType) {
        return postType == null || postType == PostType.USER_POST;
    }

    private void populatePostTotals(Post post) {
        post.setTotalComments(commentRepository.countByPostId(post.getId()));
        post.setTotalReactions(postLikeRepository.countByPostId(post.getId()));
        post.setTotalShares(postShareRepository.countByPostId(post.getId()));
    }
}
