package com.example.rest.controller;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.response.SignUpResponse;
import com.example.rest.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class FaceBookController {

    @Autowired
    private IUserService userService;

    @PostMapping(path = "/signup")
    public ResponseEntity<CommonResponse<SignUpResponse>> signUp(
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "uuid") String uuid) {
        return new ResponseEntity<>(userService.signUp(phoneNumber,password,uuid), HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<CommonResponse<SignUpResponse>> login(
            @RequestParam(name = "phoneNumber") String phoneNumber,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "deviceId") String deviceId) {
        return null;
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<CommonResponse<SignUpResponse>> logout(
            @RequestParam(name = "token") String token) {
        return null;
    }

    //Xem lại
    @PostMapping(path = "/add-post")
    public ResponseEntity<CommonResponse<SignUpResponse>> addPost(
            @RequestParam(name = "token") String token) {
        return null;
    }

    @PostMapping(path = "/delete-post")
    public ResponseEntity<CommonResponse<SignUpResponse>> deletePost(
            @RequestParam(name = "token") String token,@RequestParam(name = "id") String postId) {
        return null;
    }

    @PostMapping(path = "/edit-post")
    public ResponseEntity<CommonResponse<SignUpResponse>> editPost(
            @RequestParam(name = "token") String token,@RequestParam(name = "id") String postId) {
        return null;
    }

    @PostMapping(path = "/set-comment")
    public ResponseEntity<CommonResponse<SignUpResponse>> like(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "id") String postId,
            @RequestParam(name = "comment") String comment,
            @RequestParam(name = "index") String index,
            @RequestParam(name = "count") String count) {
        return null;
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
    public ResponseEntity<CommonResponse<SignUpResponse>> like(
            @RequestParam(name = "token") String token,@RequestParam(name = "id") String postId) {
        return null;
    }

    @PostMapping(path = "/get-list-posts")
    public ResponseEntity<CommonResponse<SignUpResponse>> getListPosts(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "in_campaign") String inCampaign,
            @RequestParam(name = "campaign_id") String campaignId,
            @RequestParam(name = "latitude") String latitude,
            @RequestParam(name = "longitude") String longitude,
            @RequestParam(name = "last_id") String lastId,
            @RequestParam(name = "index") String index,
            @RequestParam(name = "count") String count) {
        return null;
    }

    @PostMapping(path = "/check-new-item")
    public ResponseEntity<CommonResponse<SignUpResponse>> checkNewItem(
            @RequestParam(name = "last_id") String lastId,
            @RequestParam(name = "category_id") String categoryId) {
        return null;
    }
}