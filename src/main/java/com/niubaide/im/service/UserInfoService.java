package com.niubaide.im.service;

import com.niubaide.im.model.vo.ResponseJson;

public interface UserInfoService {
    ResponseJson getByUserId(String userId);

}
