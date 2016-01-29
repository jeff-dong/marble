package com.github.jxdong.marble.controller;

import com.github.jxdong.common.util.JacksonUtil;
import com.github.jxdong.common.util.StringUtils;
import com.github.jxdong.marble.domain.dto.AppDetailDTO;
import com.github.jxdong.marble.domain.dto.ServerDetailDTO;
import com.github.jxdong.marble.domain.model.*;
import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.domain.repositories.AppRepository;
import com.github.jxdong.marble.global.exception.MarbleException;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import com.github.jxdong.marble.global.util.DTOConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/app")
public class AppController extends BasicController{
    private static Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppRepository appRepository;

    /**
     * 页面跳转 - app
     * @return view
     */
    @RequestMapping("")
    public ModelAndView pageForward() throws Exception{
        return modelAndView("app");
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public Response query(String jsonParam) throws Exception{
        AppRequest request = JacksonUtil.json2pojo(URLDecoder.decode(jsonParam, "UTF-8"), AppRequest.class);
        if(request == null){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        List<AppDetail> appDetails = new ArrayList<>();
        //如果appCode不为空，先根据appCode查询
        if(request.getPrimaryKey()>0){
            AppDetail appDetail = appRepository.queryAppByCode(String.valueOf(request.getPrimaryKey()));
            appDetails.add(appDetail);
            request.getPage().setTotalRecord(1);
        }else{
            String appOwner = request.getAppOwner();
            if(!loginedAccount.getHasAdminRole()){
                appOwner = loginedAccount.getEmployee();
            }
            //如果appcode不为空，校验appcode的合法性
            if(StringUtils.isNotBlank(request.getAppCode())){
                AuthorityUtil.getInstance().validateAuthority(request.getAppCode());
            }
            appDetails = appRepository.queryAppByMultiConditions(request.getAppCode(), request.getAppName(), appOwner, request.getStatusList(),
                    request.getOrderColumn(),
                    request.getOrderDir(),
                    request.getPage());
        }
        return new Response(DTOConvert.entity2DTO(appDetails, AppDetailDTO.class), request.getPage());
    }

    @RequestMapping(value = "server/query", method = RequestMethod.GET)
    @ResponseBody
    public Response queryServerByAppId(@RequestParam(value="appCode") String appCode) throws Exception{
        if(StringUtils.isBlank(appCode)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        List<ServerDetail> serverDetailList = appRepository.queryServerByAppCode(appCode);
        return new Response(DTOConvert.entity2DTO(serverDetailList, ServerDetailDTO.class));
    }

    //添加新的应用记录
    @RequestMapping(value = "add", method = RequestMethod.PATCH)
    @ResponseBody
    public Response addApp(AppDetail appDetail) throws Exception{
        //参数校验
        if(appDetail == null || StringUtils.isBlank(appDetail.getCode()) || StringUtils.isBlank(appDetail.getName())){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        //admin角色校验
        if(loginedAccount== null || !loginedAccount.getHasAdminRole()){
            throw new MarbleException(ErrorEnum.NO_PERMISSION, "");
        }
        Result result = appRepository.insertApp(appDetail);
        return Response.resultResponse(result);
    }

    //编辑应用
    @RequestMapping(value = "edit", method = RequestMethod.PATCH)
    @ResponseBody
    public Response editApp(AppDetail appDetail) throws Exception{
        if(appDetail == null || StringUtils.isBlank(appDetail.getCode())){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appDetail.getCode());

        appDetail.setStatus(0);
        //如果非管理员不能设置owner的值
        if(!loginedAccount.getHasAdminRole()){
            appDetail.setOwner(null);
        }
        Result result = appRepository.updateApp(appDetail);
        return Response.resultResponse(result);
    }

    //删除应用
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteApp(String appCode) throws Exception{
        if(StringUtils.isBlank(appCode)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }

        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = appRepository.deleteApp(appCode);
        return Response.resultResponse(result);
    }

    //添加新的应用server记录
    @RequestMapping(value = "server/add", method = RequestMethod.PATCH)
    @ResponseBody
    public Response addAppServer(ServerDetail serverDetail) throws Exception{
        //参数校验
        if(serverDetail == null || !serverDetail.validateParamForInsert()){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }

        AuthorityUtil.getInstance().validateAuthority(serverDetail.getAppCode());
        Result result = appRepository.addAppServer(serverDetail);
        return Response.resultResponse(result);
    }

    //删除应用server
    @RequestMapping(value = "server/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteAppServer(String appCode, String serverGroup, String serverName, String serverIp) throws Exception{
        if(StringUtils.isBlank(appCode) || StringUtils.isBlank(serverGroup) || StringUtils.isBlank(serverName) ||StringUtils.isBlank(serverIp) ){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = appRepository.deleteAppServer(appCode, serverGroup, serverName, serverIp);
        return Response.resultResponse(result);
    }

    //启用应用
    @RequestMapping(value = "start", method = RequestMethod.PATCH)
    @ResponseBody
    public Response startApp(String appCode) throws Exception{
        if(StringUtils.isBlank(appCode)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = appRepository.startApp(appCode);
        return Response.resultResponse(result);
    }

    //停用应用
    @RequestMapping(value = "stop", method = RequestMethod.PATCH)
    @ResponseBody
    public Response stopApp(String appCode) throws Exception{
        if(StringUtils.isBlank(appCode)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        AuthorityUtil.getInstance().validateAuthority(appCode);
        Result result = appRepository.stopApp(appCode);
        return Response.resultResponse(result);
    }
}
