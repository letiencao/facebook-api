package com.example.rest.service;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.response.post.AddPostResponse;
import com.example.rest.model.response.post.GetPostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPostService {
    CommonResponse<AddPostResponse> addPost(String token, MultipartFile[] image, MultipartFile video, String described,
                                            String status) throws Exception;

    CommonResponse<GetPostResponse> getPost(String token, String postId);

    CommonResponse editPost(String token, String postId, String described, String status, MultipartFile[] image, List<String> imageIdsDeleted,
                            List<String> imageIdsSort, MultipartFile video, MultipartFile thumb,
                            String autoAccept, String autoBlock) throws Exception;

}
