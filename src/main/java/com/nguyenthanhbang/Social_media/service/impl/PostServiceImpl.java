package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.PostMediaRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostMedia;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.PostMediaRepository;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMediaRepository postMediaRepository;

    @Override
    public Post createPost(CreatePostRequest request) {
        User user = userService.getUserLogin();
        Post post = new Post();
       // post.setActive(true);
        post.setContent(request.getContent());
        post.setPrivacy(request.getPrivacy());
        post.setUser(user);
        log.info("create post");
        List<PostMedia> postMediaList = new ArrayList<>();
        for(PostMediaRequest item : request.getMedia()) {
            PostMedia postMedia = PostMedia.builder()
                    .mediaUrl(item.getMediaUrl())
                    .mediaType(item.getMediaType())
                    .uploadOrder(item.getUploadOrder())
                    .post(post)
                    .build();
            postMediaList.add(postMedia);
        }
        post.setMedia(postMediaList);
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long postId, UpdatePostRequest request) {
        log.info("Update post");
        Post post = this.getPostById(postId);
        User user = userService.getUserLogin();
        if(user.getId() != post.getUser().getId()){
            throw new EntityNotFoundException("You do not have permission to update this post");
        }
        if (post.getMedia() == null) {
            post.setMedia(new ArrayList<>());
        }
        post.setPrivacy(request.getPrivacy());
        post.setContent(request.getContent());
        List<PostMedia> postMediaList = post.getMedia();
        Iterator<PostMedia> currentMediaIterator = postMediaList.iterator();
        List<String> urls = request.getMedia().stream().map(item -> item.getMediaUrl()).collect(Collectors.toList());
        while(currentMediaIterator.hasNext()){
            PostMedia item = currentMediaIterator.next();
            if(!urls.contains(item.getMediaUrl())){
                currentMediaIterator.remove();
            }
        }
        List<String> currentUrls = postMediaList.stream().map(item -> item.getMediaUrl()).collect(Collectors.toList());
        for(PostMediaRequest item : request.getMedia()){
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
        return postRepository.save(post);
    }

    @Override
    public List<Post> getNewsFeed() {
        return null;
    }

    @Override
    public List<Post> getPostByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return postRepository.findByUserId(userId);
    }

    @Override
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
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
}
