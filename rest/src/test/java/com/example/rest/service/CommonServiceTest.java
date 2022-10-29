//package com.example.rest.service;
//
//import com.example.rest.common.CommonService;
//import com.example.rest.common.Constant;
//import org.junit.Assert;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//
//public class CommonServiceTest {
//    @InjectMocks
//    private CommonService commonService;
//
//    @Test
//    void checkCommonValidateTest(){
//        //TODO Test Case tham số truyền vào là null || rỗng
//        int expectedCode = Constant.PARAMETER_IS_NOT_ENOUGH_CODE;
//        String testParam1 = null;
//        String testParam2 = "";
//        int actualCode1 = commonService.checkCommonValidate(testParam1).getCode();
//        int actualCode2 = commonService.checkCommonValidate(testParam2).getCode();
//        Assert.assertEquals(expectedCode,actualCode1);
//        Assert.assertEquals(expectedCode,actualCode2);
//    }
//}
