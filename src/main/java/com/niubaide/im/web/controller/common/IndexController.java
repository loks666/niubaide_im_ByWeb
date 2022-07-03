package com.niubaide.im.web.controller.common;

import org.springframework.web.bind.annotation.RequestMapping;

public class IndexController {

    @RequestMapping("/")
    public String hello(){
        return "forward:index2.html";
    }

}
