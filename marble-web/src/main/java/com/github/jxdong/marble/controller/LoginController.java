package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.common.util.PropertyUtils;
import com.github.jxdong.marble.domain.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;

/**
 * SSO的登陆/退出
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/12/30 14:05
 */
@Controller
@RequestMapping("/account")
public class LoginController extends BasicController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public Response logout() {
        logger.info("logout...");
        return Response.SUCCESS;
    }

}
