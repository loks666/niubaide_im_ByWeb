package com.niubaide.im.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.niubaide.im.dao.UserInfoDao;
import com.niubaide.im.model.po.UserInfo;
import com.niubaide.im.model.vo.ResponseJson;
import com.niubaide.im.service.SecurityService;
import com.niubaide.im.util.Constant;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;

@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserInfoDao userInfoDao;


    @Override
    public ResponseJson login(String username, String password, HttpSession session) {
        UserInfo userInfo = userInfoDao.getByUsername(username);
        if (userInfo == null) {
            return new ResponseJson().error("不存在该用户名");
        }
        if (!userInfo.getPassword().equals(password)) {
            return new ResponseJson().error("密码不正确");
        }
        session.setAttribute(Constant.USER_TOKEN, userInfo.getUserId());
        return new ResponseJson().success();
    }

    @Override
    public ResponseJson logout(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        if (userId == null) {
            return new ResponseJson().error("请先登录！");
        }
        session.removeAttribute(Constant.USER_TOKEN);
        log.info(MessageFormat.format("userId为 {0} 的用户已注销登录!", userId));
        return new ResponseJson().success();
    }

}
