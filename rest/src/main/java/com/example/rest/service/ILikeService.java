package com.example.rest.service;

import com.example.rest.common.CommonException;
import com.example.rest.common.CommonResponse;
import com.example.rest.model.entity.Likes;
import com.example.rest.model.response.CommentResponse;

public interface ILikeService {
    CommonResponse<String> saveLike(String token, String id) throws CommonException;

    String getNumberLikes(String postId);
}
