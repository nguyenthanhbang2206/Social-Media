package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.GroupClient;
import com.nguyenthanhbang.Social_media.client.InteractionClient;
import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.GroupSummaryResponse;
import com.nguyenthanhbang.Social_media.common.dto.PostInteractionCountResponse;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupPrivacy;
import com.nguyenthanhbang.Social_media.common.enumeration.PostType;
import com.nguyenthanhbang.Social_media.common.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.common.event.PostDeletedEvent;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.PostMediaRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.event.PostDeletedPublisher;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostMedia;
import com.nguyenthanhbang.Social_media.repository.PostMediaRepository;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import com.nguyenthanhbang.Social_media.repository.PostShareRepository;
import com.nguyenthanhbang.Social_media.service.PostService;
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
    private final PostMediaRepository postMediaRepository;
    private final PostShareRepository postShareRepository;
    private final GroupClient groupClient;
    private final InteractionClient interactionClient;
    private final UserClient userClient;
    private final PostDeletedPublisher postDeletedPublisher;

    @Override
    public Post createPost(Long groupId, CreatePostRequest request) {
        Long userId = getCurrentUserId();
        ApiResponse<UserSummaryResponse> userResponse = userClient.getUserById(userId);
        UserSummaryResponse userSummaryResponse = userResponse != null ? userResponse.getData() : null;
        Post post = new Post();
        post.setContent(request.getContent());
        PrivacyLevel privacy = request.getPrivacy() == null ? PrivacyLevel.PUBLIC : request.getPrivacy();
        post.setPrivacy(isUserPost(request.getPostType()) ? privacy : PrivacyLevel.PUBLIC);
        post.setUserId(userId);
        post.setOwnerName(userSummaryResponse == null ? null : userSummaryResponse.getFullName());
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
            GroupSummaryResponse group = groupClient.getGroupById(groupId).getData();
            if (group == null) {
                throw new EntityNotFoundException("Group not found");
            }
            GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
            if (status != GroupMembershipStatus.APPROVED) {
                throw new EntityNotFoundException("You have to join this group to create new post");
            }
            post.setGroupId(groupId);
            post.setPostType(PostType.GROUP_POST);
            if(group.getPrivacy() == GroupPrivacy.PRIVATE){
                boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
                post.setIsApproved(isAdmin);
            }else {
                post.setIsApproved(true);
            }
        } else if(isUserPost(request.getPostType())){
            post.setPostType(PostType.USER_POST);
        }
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long groupId, Long postId, UpdatePostRequest request) {
        log.info("Update post");
        Post post = this.getPostById(postId);
        Long userId = getCurrentUserId();
        if(!userId.equals(post.getUserId())){
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
            GroupSummaryResponse group = groupClient.getGroupById(groupId).getData();
            if (group == null) {
                throw new EntityNotFoundException("Group not found");
            }
            GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
            if (status != GroupMembershipStatus.APPROVED) {
                throw new EntityNotFoundException("You have to join this group to update this post");
            }
            post.setGroupId(groupId);
            post.setPostType(PostType.GROUP_POST);
            if(group.getPrivacy() == GroupPrivacy.PRIVATE){
                boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
                post.setIsApproved(isAdmin);
            }else{
                post.setIsApproved(true);
            }
        } else if(isUserPost(request.getPostType())){
            post.setPostType(PostType.USER_POST);
        }
        return postRepository.save(post);
    }

    @Override
    public List<Post> getPostApprovedForGroup(Long groupId) {
        GroupSummaryResponse group = groupClient.getGroupById(groupId).getData();
        if (group == null) {
            throw new EntityNotFoundException("Group not found");
        }
        GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
        if(group.getPrivacy() == GroupPrivacy.PRIVATE && status != GroupMembershipStatus.APPROVED){
            throw new EntityNotFoundException("Group is private. Join to view group post");
        }
        return postRepository.findByGroupIdAndIsApproved(groupId, true);
    }

    @Override
    public List<Post> getPostPending(Long groupId) {
        GroupSummaryResponse group = groupClient.getGroupById(groupId).getData();
        if (group == null) {
            throw new EntityNotFoundException("Group not found");
        }
        GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
        boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
        if(!isAdmin || status != GroupMembershipStatus.APPROVED){
            throw new IllegalStateException("You can not view pending post");
        }
        return postRepository.findByGroupIdAndIsApproved(groupId, false);
    }

    @Override
    public void approvePost(Long groupId, Long postId) {
        Post post = postRepository.findByGroupIdAndId(groupId, postId).orElseThrow(()->new EntityNotFoundException("Post not found"));
        GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
        boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
        if(status != GroupMembershipStatus.APPROVED){
            throw new IllegalStateException("You can not approve post");
        }
        if(!isAdmin){
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
        GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
        boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
        if (status != GroupMembershipStatus.APPROVED || !isAdmin) {
            throw new IllegalStateException("You can not pin post");
        }
        post.setIsPinned(true);
        postRepository.save(post);
    }

    @Override
    public void unpinPost(Long groupId, Long postId) {
        Post post = postRepository.findByGroupIdAndId(groupId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        GroupMembershipStatus status = groupClient.getMembershipStatus(groupId).getData();
        boolean isAdmin = Boolean.TRUE.equals(groupClient.isAdmin(groupId).getData());
        if (status != GroupMembershipStatus.APPROVED || !isAdmin) {
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
        postDeletedPublisher.publishPostDeleted(PostDeletedEvent.builder()
                        .postId(postId)
                .build());
    }

    private boolean isUserPost(PostType postType) {
        return postType == null || postType == PostType.USER_POST;
    }

    private void populatePostTotals(Post post) {
        PostInteractionCountResponse counts = interactionClient.getPostCounts(post.getId()).getData();
        long totalComments = counts == null || counts.getTotalComments() == null ? 0L : counts.getTotalComments();
        long totalReactions = counts == null || counts.getTotalReactions() == null ? 0L : counts.getTotalReactions();
        post.setTotalComments(totalComments);
        post.setTotalReactions(totalReactions);
        post.setTotalShares(postShareRepository.countByPostId(post.getId()));
    }

    private Long getCurrentUserId() {
        // Use email to lookup user since X-User-Id is no longer sent (it was UUID, not Long)
        String email = RequestHeaderUtil.getUserEmail()
                .orElseThrow(() -> new EntityNotFoundException("User not found - X-User-Email header missing"));
        
        ApiResponse<UserSummaryResponse> response = userClient.getUserByEmail(email);
        if (response == null || response.getData() == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        
        return response.getData().getId();
    }
}
