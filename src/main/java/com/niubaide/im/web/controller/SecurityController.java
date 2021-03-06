package com.niubaide.im.web.controller;

import com.niubaide.im.model.vo.ResponseJson;
import com.niubaide.im.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class SecurityController {

    @Autowired
    SecurityService securityService;

    @RequestMapping(value = {"toLogin", "/"}, method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson login(HttpSession session,
                              @RequestParam String username,
                              @RequestParam String password) {
        return securityService.login(username, password, session);
    }

    /**
     * 描述：登录成功后，调用此接口进行页面跳转
     *
     * @return
     */
    @RequestMapping(value = "chatroom", method = RequestMethod.GET)
    public String toChatroom() {
        return "chatroom";
    }

    @RequestMapping(value = "logout")
    public ResponseJson logout(HttpSession session) {
        return securityService.logout(session);
    }
}
