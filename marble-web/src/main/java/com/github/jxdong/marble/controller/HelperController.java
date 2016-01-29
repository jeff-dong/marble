package com.github.jxdong.marble.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/helper")
public class HelperController extends BasicController{

    /**
     * 页面跳转 - helper
     * @return view
     */
    @RequestMapping("")
    public ModelAndView pageForward() throws Exception{
        return modelAndView("helper");
    }


}
