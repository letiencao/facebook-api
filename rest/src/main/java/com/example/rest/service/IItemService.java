package com.example.rest.service;

import com.example.rest.common.CommonResponse;

public interface IItemService {
    CommonResponse<String> checkNewItem(String lastId, String categoryId);
}
