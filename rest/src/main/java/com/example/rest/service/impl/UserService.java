package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.model.CustomUserDetails;
import com.example.rest.model.entity.User;
import com.example.rest.model.response.LoginResponse;
import com.example.rest.model.response.SignUpResponse;
import com.example.rest.repository.UserRepository;
import com.example.rest.security.JwtProvider;
import com.example.rest.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(s);
        return user != null ? new CustomUserDetails(user) : null;
    }

    @Override
    public CommonResponse<SignUpResponse> signUp(String phoneNumber, String password, String uuid) {

        //Check common validate ~ check những trường bắt buộc đều phải khác null -> Check từng trường hợp chi tiết
        commonService.checkCommonValidate(phoneNumber,password,uuid);
        //Check phone number validate
        commonService.checkPhoneNumberValid(phoneNumber);
        //Check password validate
        commonService.checkPasswordValid(password);

        if(loadUserByUsername(phoneNumber) != null){
            return new CommonResponse(Constant.USER_EXISTED_CODE,Constant.USER_EXISTED_MESSAGE,null);
        }
//        String token = jwtProvider.generateAccessToken(phoneNumber,uuid);
        User user = setCommonUserInfo(phoneNumber);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        if(userRepository.save(user) == null){
            return new CommonResponse(Constant.EXCEPTION_ERROR_CODE,Constant.EXCEPTION_ERROR_MESSAGE,null);
        }
        List<SignUpResponse> list = new ArrayList<>();
        SignUpResponse signUpResponse = new SignUpResponse();
        list.add(signUpResponse);
        return new CommonResponse<>(Constant.OK_CODE, Constant.OK_MESSAGE, list);
    }

    @Override
    public CommonResponse<LoginResponse> login(String phoneNumber, String password, String deviceId) {
        CommonResponse<LoginResponse> commonResponse = new CommonResponse();
        List<LoginResponse> list = new ArrayList<>();
        //Check common validate ~ check những trường bắt buộc đều phải khác null -> Check từng trường hợp chi tiết
        commonService.checkCommonValidate(phoneNumber,password,deviceId);

        //Check phone number validate
        commonService.checkPhoneNumberValid(phoneNumber);
        //Check password validate
        commonService.checkPasswordValid(password);

        User user = userRepository.findByPhoneNumberAndPassword(phoneNumber,password);
        String token = jwtProvider.generateAccessToken(phoneNumber);
        if(user == null){
            return new CommonResponse(Constant.USER_IS_NOT_VALIDATED_CODE,Constant.USER_IS_NOT_VALIDATED_MESSAGE,null);
        }
        user.setUuid(deviceId);
        user.setModifiedBy(phoneNumber);
        user.setModifiedDate(System.currentTimeMillis());
        userRepository.save(user);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(String.valueOf(user.getId()));
        loginResponse.setAvatar(user.getLinkAvatar());
        loginResponse.setUsername(user.getPhoneNumber());
        loginResponse.setToken(token);
        list.add(loginResponse);
        commonResponse.setData(list);
        commonResponse.setMessage(Constant.OK_MESSAGE);
        commonResponse.setCode(Constant.OK_CODE);
        return commonResponse;
    }

    //Set các thông tin chung khi thêm user
    private User setCommonUserInfo(String phoneNumber){
        User user = new User();
        user.setDeleted(false);
        user.setLinkAvatar("-1");
        user.setCreatedDate(System.currentTimeMillis());
        user.setCreatedBy(phoneNumber);

        return user;
    }
}
