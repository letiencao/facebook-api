package com.example.rest.model.mapper;


import com.example.rest.model.entity.Post;
import com.example.rest.model.response.posts.PostsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    @Autowired
    ObjectMapper objectMapper;

    public PostsResponse toResponse(Post post) {
        PostsResponse postsResponse = new PostsResponse();
        postsResponse.setId(String.valueOf(post.getId()));
        postsResponse.setComment(String.valueOf(10));
        postsResponse.setContent(post.getContent());
        //converto to image or video
        postsResponse.setUser_id(String.valueOf(post.getUserId()));
        return postsResponse;
    }

    public Post toPost(PostsResponse postsResponse) {
        return objectMapper.convertValue(postsResponse, Post.class);
    }

}