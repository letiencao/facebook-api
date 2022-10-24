package com.example.rest.controller;

import com.example.rest.common.CommonResponse;

import com.example.rest.model.response.*;
import com.example.rest.model.response.posts.DataResponse;
import com.example.rest.service.ICommentService;

import com.example.rest.model.response.post.AddPostResponse;
import com.example.rest.model.response.LoginResponse;
import com.example.rest.model.response.SignUpResponse;
import com.example.rest.service.ILikeService;
import com.example.rest.service.IPostService;

import com.example.rest.service.IUserService;
import com.example.rest.service.impl.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(path = "/")
public class FaceBookController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IPostService postService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ILikeService iLikeService;

    @Autowired
    ItemService itemService;

    @PostMapping(path = "/signup")
    public ResponseEntity<CommonResponse<SignUpResponse>> signUp(
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "uuid") String uuid) {
        return new ResponseEntity<>(userService.signUp(phoneNumber, password, uuid), HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "deviceId") String deviceId) {
        return new ResponseEntity<>(userService.login(phoneNumber, password, deviceId), HttpStatus.OK);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<CommonResponse<SignUpResponse>> logout(
            @RequestParam(name = "token") String token) {
        return null;
    }

    @PostMapping(path = "/add-post")
    public ResponseEntity<CommonResponse<AddPostResponse>> addPost(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "image") MultipartFile[] image,
            @RequestParam(name = "video") MultipartFile video,
            @RequestParam(name = "described") String described,
            @RequestParam(name = "status") String status) throws Exception {
        return new ResponseEntity<>(postService.addPost(token, image, video, described, status), HttpStatus.OK);
    }

    @PostMapping(path = "/delete-post")
    public ResponseEntity<CommonResponse<SignUpResponse>> deletePost(
            @RequestParam(name = "token") String token, @RequestParam(name = "id") String postId) {
        return null;
    }

    @PostMapping(path = "/edit-post")
    public ResponseEntity<CommonResponse<SignUpResponse>> editPost(
            @RequestParam(name = "token") String token, @RequestParam(name = "id") String postId) {
        return null;
    }

    @PostMapping(path = "/set-comment")
    public ResponseEntity<CommonResponse<CommentResponse>> like(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "id") String postId,
            @RequestParam(name = "comment") String comment,
            @RequestParam(name = "index") String index,
            @RequestParam(name = "count") String count) {
        return new ResponseEntity<>(commentService.setComment(token, postId, comment, index, count), HttpStatus.OK);
    }

    @PostMapping(path = "/report-post")
    public ResponseEntity<CommonResponse<SignUpResponse>> reportPost(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "id") String postId,
            @RequestParam(name = "subject") String subject,
            @RequestParam(name = "details") String details) {
        return null;
    }

    @PostMapping(path = "/like")
    public ResponseEntity<CommonResponse<String>> like(
            @RequestParam(name = "token") String token, @RequestParam(name = "id") String postId) {
        return new ResponseEntity<>(iLikeService.saveLike(token, postId), HttpStatus.OK);
    }

    @PostMapping(path = "/get-list-posts")
    public ResponseEntity<CommonResponse<DataResponse>> getListPosts(
            @RequestParam(required = false, name = "token") String token,
            @RequestParam(required = false, name = "user_id") String user_id,
            @RequestParam(required = false, name = "campaign_id") String in_campaign,
            @RequestParam(required = false, name = "campaign_id") String campaign_id,
            @RequestParam(required = false, name = "latitude") String latitude,
            @RequestParam(required = false, name = "longitude") String longitude,
            @RequestParam(required = false, name = "last_id") String last_id,
            @RequestParam(defaultValue = "1", required = false, name = "index") String index,
            @RequestParam(defaultValue = "20", required = false, name = "count") String count) {
        return new ResponseEntity<>(postService.getListPosts(token, user_id, in_campaign, campaign_id, latitude, longitude, last_id, index, count), HttpStatus.OK);
    }

    @PostMapping(path = "/check-new-item")
    public ResponseEntity<CommonResponse<String>> checkNewItem(
            @RequestParam(name = "last_id") String lastId,
            @RequestParam(name = "category_id") String categoryId) {
        return new ResponseEntity<>(itemService.checkNewItem(lastId, categoryId), HttpStatus.OK);
    }
}
