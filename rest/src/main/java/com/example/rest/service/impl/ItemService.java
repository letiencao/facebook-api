package com.example.rest.service.impl;

import com.example.rest.common.CommonResponse;
import com.example.rest.common.CommonService;
import com.example.rest.common.Constant;
import com.example.rest.repository.PostRepository;
import com.example.rest.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService implements IItemService {
    @Autowired
    private CommonService commonService;

    @Autowired
    private PostRepository postRepository;

    @Override
    public CommonResponse<String> checkNewItem(String lastId, String categoryId) {
        this.validateParamCheckNewItem(lastId, categoryId);

        //validate categoryId
        if (!StringUtils.isEmpty(categoryId)
                && (Integer.parseInt(categoryId)) > 3
                || Integer.parseInt(categoryId) < 0) {
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
        } else if (StringUtils.isEmpty(categoryId)) {
            categoryId = String.valueOf(0);
        }
        List<String> result = new ArrayList<>();
        result.add(String.valueOf(postRepository.findNewPosts(lastId).size()));
        return new CommonResponse(Constant.OK_CODE, Constant.OK_MESSAGE, result);
    }

    private CommonResponse validateParamCheckNewItem(String lastId, String categoryId) {
        this.commonService.checkCommonValidate(lastId);
        CommonResponse commonResponse = new CommonResponse();

        //check exist last-post-id
        if (this.postRepository.findById(Integer.parseInt(lastId)) == null) {
            return new CommonResponse(Constant.PARAMETER_VALUE_IS_INVALID_CODE, Constant.PARAMETER_VALUE_IS_INVALID_MESSAGE, null);
        }
        return commonResponse;
    }
}
