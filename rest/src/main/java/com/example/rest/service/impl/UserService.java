package com.example.rest.service.impl;

import com.example.rest.common.CommonException;
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
import lombok.AllArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    public CommonResponse<SignUpResponse> signUp(String phoneNumber, String password, String uuid) throws CommonException {

        //Check common validate ~ check những trường bắt buộc đều phải khác null -> Check từng trường hợp chi tiết
        commonService.checkCommonValidate(phoneNumber,password,uuid);
        //Check phone number validate
        commonService.checkPhoneNumberValid(phoneNumber);
        //Check password validate
        commonService.checkPasswordValid(password);

        if(loadUserByUsername(phoneNumber) != null){
            throw new CommonException(Constant.USER_EXISTED_CODE,Constant.USER_EXISTED_MESSAGE);
        }
//        String token = jwtProvider.generateAccessToken(phoneNumber,uuid);
        User user = setCommonUserInfo(phoneNumber);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        if(userRepository.save(user) == null){
            throw new CommonException(Constant.EXCEPTION_ERROR_CODE,Constant.EXCEPTION_ERROR_MESSAGE);
        }
        List<SignUpResponse> list = new ArrayList<>();
        SignUpResponse signUpResponse = new SignUpResponse();
        list.add(signUpResponse);
        return new CommonResponse<>(Constant.OK_CODE, Constant.OK_MESSAGE, list);
    }

    @Override
    public CommonResponse<LoginResponse> login(String phoneNumber, String password, String deviceId) throws CommonException {
        CommonResponse<LoginResponse> commonResponse = new CommonResponse();
        List<LoginResponse> list = new ArrayList<>();
        //Check common validate ~ check những trường bắt buộc đều phải khác null -> Check từng trường hợp chi tiết
        commonService.checkCommonValidate(phoneNumber,password,deviceId);

        if (phoneNumber == password){
            throw new CommonException(Constant.PARAMETER_VALUE_IS_INVALID_CODE , Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE);
        }

        //Check phone number validate
        commonService.checkPhoneNumberValid(phoneNumber);
        //Check password validate
        commonService.checkPasswordValid(password);

        User user = userRepository.findByPhoneNumberAndPassword(phoneNumber,password);
        String token = jwtProvider.generateAccessToken(phoneNumber);

        if(user == null){
            throw new CommonException(Constant.USER_IS_NOT_VALIDATED_CODE,Constant.USER_IS_NOT_VALIDATED_MESSAGE);
        }


        user.setUuid(deviceId);
        user.setModifiedBy(phoneNumber);
        user.setModifiedDate(System.currentTimeMillis());
        user.setToken(token);
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

    @Override
    public CommonResponse logout(String token) throws CommonException {
        commonService.checkCommonValidate(token);


        int userId = Integer.parseInt(commonService.getUserIdFromToken(token));

        if (userId == 0){
            throw new CommonException(Constant.USER_IS_NOT_VALIDATED_CODE, Constant.USER_IS_NOT_VALIDATED_MESSAGE);
        }

        User user = userRepository.findById(userId);

        if (user == null){
            throw new CommonException(Constant.USER_IS_NOT_VALIDATED_CODE, Constant.USER_IS_NOT_VALIDATED_MESSAGE);
        }

        user.setToken("");
        user.setModifiedBy(String.valueOf(userId));
        user.setModifiedDate(System.currentTimeMillis());
        userRepository.save(user);


        return new CommonResponse(Constant.OK_CODE, Constant.OK_MESSAGE, null);
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
