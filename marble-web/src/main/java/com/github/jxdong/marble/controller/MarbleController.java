package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.global.exception.MarbleException;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/marble")
public class MarbleController extends BasicController{
    private static Logger logger = LoggerFactory.getLogger(MarbleController.class);


    //校验管理员权限
    @ModelAttribute
    public void authority() throws Exception {
        loginedAccount = AuthorityUtil.getInstance().getLoginedAccount();
        if(loginedAccount == null || !loginedAccount.getHasAdminRole()){
            throw new MarbleException(ErrorEnum.NO_PERMISSION,"");
        }
    }

}
