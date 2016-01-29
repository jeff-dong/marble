package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.domain.model.LoginAccount;
import com.github.jxdong.marble.domain.model.Response;
import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.global.exception.MarbleException;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/25 14:26
 */
public abstract class BasicController {
    protected LoginAccount loginedAccount = null;
    protected HttpServletResponse httpResponse;
    protected HttpServletRequest httpRequest;
    protected HttpSession httpSession;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response){
        this.httpRequest = request;
        this.httpResponse = response;
        this.httpSession = request.getSession();
    }

    @ModelAttribute
    public void setLoginedAccount() throws Exception{
       loginedAccount = AuthorityUtil.getInstance().getLoginedAccount();
       if(loginedAccount == null){
           throw new MarbleException(ErrorEnum.SESSON_OVERDUE, "请先登录");
       }
    }

    protected ModelAndView errorModelAndView(Response.ResultCodeEnum errorEnum, String detail){
        String resultPage = "error";
        Map<String, String> errorMap = new HashMap<>();
        if(errorEnum != null){
            errorMap.put("errorCode", errorEnum.getCode());
            errorMap.put("errorMsg", errorEnum.getDesc() + (StringUtils.isNotBlank(detail)?"： "+ detail:""));
        }
        return new ModelAndView(resultPage, errorMap);
    }

    protected ModelAndView modelAndView(String page){
        if(StringUtils.isNotBlank(page)){
            return new ModelAndView(page, "LoginAccount", AuthorityUtil.getInstance().getLoginedAccount());
        }
        return errorModelAndView(Response.ResultCodeEnum.UNKNOWN_ERROR, "");
    }

    protected ModelAndView modelAndView(String page, Map<String, Object> data){
        if(StringUtils.isNotBlank(page) && data != null){
            data.put("LoginAccount", AuthorityUtil.getInstance().getLoginedAccount());
            return new ModelAndView(page, data);
        }
        return errorModelAndView(Response.ResultCodeEnum.UNKNOWN_ERROR, "");
    }

}
