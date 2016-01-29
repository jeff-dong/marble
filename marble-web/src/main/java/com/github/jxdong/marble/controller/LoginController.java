package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.domain.model.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SSO的登陆/退出
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/12/30 14:05
 */
@Controller
@RequestMapping("/account")
public class LoginController extends BasicController {

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public Response logout() {
        //TODO 实现自己的登出逻辑
        return Response.SUCCESS;
    }

}
