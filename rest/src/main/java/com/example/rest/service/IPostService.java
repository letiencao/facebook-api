package com.example.rest.service;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.response.AddPostResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface IPostService {
    CommonResponse<AddPostResponse> addPost(String token, MultipartFile[] image, MultipartFile video, String described,
                                            String status);
}
