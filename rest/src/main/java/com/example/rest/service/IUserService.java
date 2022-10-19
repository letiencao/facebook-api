package com.example.rest.service;

import com.example.rest.common.CommonResponse;
import com.example.rest.model.response.LoginResponse;
import com.example.rest.model.response.SignUpResponse;

public interface IUserService {
    CommonResponse<SignUpResponse> signUp(String phoneNumber,String password,String uuid);
    CommonResponse<LoginResponse> login(String phoneNumber,String password,String deviceId);
}
