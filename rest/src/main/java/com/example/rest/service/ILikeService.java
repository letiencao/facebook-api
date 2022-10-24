package com.example.rest.service;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.entity.Likes;
import com.example.rest.model.response.CommentResponse;

public interface ILikeService {
    CommonResponse<String> saveLike(String token, String id);

    String getNumberLikes(String postId);
}
