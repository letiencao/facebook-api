package com.example.rest.service;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.response.post.AddPostResponse;
import com.example.rest.model.response.post.GetPostResponse;
import com.example.rest.model.response.posts.DataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IPostService {
    CommonResponse<AddPostResponse> addPost(String token, MultipartFile[] image, MultipartFile video, String described,
                                            String status) throws Exception;
    CommonResponse<GetPostResponse> getPost(String token, String postId);

    CommonResponse<DataResponse> getListPosts(String token,
                                              String user_id,
                                              String in_campaign,
                                              String campaign_id,
                                              String latitude,
                                              String longitude,
                                              String last_id,
                                              String index,
                                              String count);

}
