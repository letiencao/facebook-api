package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.model.entity.Post;
import com.example.rest.model.response.ReportResponse;
import com.example.rest.repository.PostRepository;
import com.example.rest.security.JwtProvider;
import com.example.rest.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportService implements IReportService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CommonService commonService;

    @Override
    public CommonResponse<ReportResponse> reportPost(String id, String token, String subject, String details){
        commonService.checkCommonValidate(id, token, subject, details);


        Post post = postRepository.findById(Integer.parseInt(id));
        if (post == null){
            return new CommonResponse<>(Constant.POST_IS_NOT_EXISTED_CODE, Constant.POST_IS_NOT_EXISTED_MESSAGE, null);
        }





        //Truong hop tra ra du lieu thanh cong








        return null;
    }
}
