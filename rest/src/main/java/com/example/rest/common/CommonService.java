package com.example.rest.common;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class CommonService {
//    private final MappingErrorCode mappingErrorCode;

    //Check != null và != "" cho các trường bắt buộc phải truyền
    //Object... thể hiện là mảng đối tượng truyền vào, tuy nhiên không bị giới hạn số lượng phần tử như static array
    //tức là truyền bao nhiêu tham số cũng được
    public CommonResponse checkCommonValidate(Object... objects) { // 1 chức năng -> 4 trường bắt buộc phải truyền
        // phải check -> static array [10]
        for(int i = 0;i < objects.length;i++){
            if(StringUtils.isEmpty(objects[i].toString())){
                return new CommonResponse(Constant.PARAMETER_IS_NOT_ENOUGH_CODE,Constant.PARAMETER_IS_NOT_ENOUGH_MESSAGE,null);
            }
        }
        return new CommonResponse(Constant.OK_CODE,Constant.OK_MESSAGE,null);

    }

    //Check validate Phone number
    public CommonResponse checkPhoneNumberValid(String phoneNumber) {
        Pattern pattern = Pattern.compile("(84|0[3|5|7|8|9])+([0-9]{8})\\b");
        Matcher matcher = pattern.matcher(phoneNumber);
        if(!matcher.matches()){
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE,Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE,null);
        }
        return new CommonResponse(Constant.OK_CODE,Constant.OK_MESSAGE,null);
    }

    //Check validate password
    public CommonResponse checkPasswordValid(String password) {

        int length = password.length();
        if(length < 6 || length > 10){
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE,Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE,null);
        }
        return new CommonResponse(Constant.OK_CODE,Constant.OK_MESSAGE,null);
    }
    //Get token from request header
    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

}
