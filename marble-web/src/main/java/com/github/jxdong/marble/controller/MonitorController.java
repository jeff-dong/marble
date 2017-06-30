package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.global.exception.MarbleException;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/monitor")
public class MonitorController extends BasicController{
    private static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    //校验管理员权限
    @ModelAttribute
    public void authority() throws Exception {
        loginedAccount = AuthorityUtil.getInstance().getLoginedAccount();
        if(loginedAccount == null || !loginedAccount.getHasAdminRole()){
            throw new MarbleException(ErrorEnum.NO_PERMISSION,"");
        }
    }
    /**
     * 页面跳转 - monitor
     * @return view
     */
    @RequestMapping("")
    public ModelAndView pageForward(){
        return modelAndView("monitor");
    }

}
