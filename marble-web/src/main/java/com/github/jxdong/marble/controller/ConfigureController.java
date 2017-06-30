package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.common.util.JacksonUtil;
import com.github.jxdong.marble.domain.dto.ConfigureDTO;
import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.domain.repositories.ConfigureRepository;
import com.github.jxdong.marble.global.exception.MarbleException;
import com.github.jxdong.marble.global.util.AuthorityUtil;
import com.github.jxdong.marble.global.util.DTOConvert;
import com.github.jxdong.marble.domain.model.Configure;
import com.github.jxdong.marble.domain.model.ConfigureRequest;
import com.github.jxdong.marble.domain.model.Response;
import com.github.jxdong.marble.domain.model.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/configure")
public class ConfigureController extends BasicController{
    private static Logger logger = LoggerFactory.getLogger(ConfigureController.class);

    @Autowired
    private ConfigureRepository configureRepository;

    //校验管理员权限
    @ModelAttribute
    public void authority() throws Exception {
        loginedAccount = AuthorityUtil.getInstance().getLoginedAccount();
        if(loginedAccount == null || !loginedAccount.getHasAdminRole()){
            throw new MarbleException(ErrorEnum.NO_PERMISSION,"");
        }
    }
    /**
     * 页面跳转 - configure
     * @return view
     */
    @RequestMapping("")
    public ModelAndView pageForward(){
        return modelAndView("configure");
    }

    /**
     * 多条件查询，支持分页
     * configure?status=1&currentPage=1&pageSize=2
     * @param jsonParam jsonParam
     * @return response
     */
    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public Response queryMultiCondition(String jsonParam){
        ConfigureRequest request = JacksonUtil.json2pojo(jsonParam, ConfigureRequest.class);
        if(request == null){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }

        List<Configure> configureList = new ArrayList<>();
        //如果primary不为空，先根据primaryKey查询
        if(request.getIntPrimaryKey() >0){
            Configure configure = configureRepository.queryConfigureById(request.getIntPrimaryKey());
            if(configure != null){
                configureList.add(configure);
                request.getPage().setTotalRecord(1);
            }
        }else{
            configureList = configureRepository.queryConfigureMultiConditions(request.getGroup(), request.getKey(), request.getOrderColumn(), request.getOrderDir(), request.getPage());
        }

        return new Response(DTOConvert.entity2DTO(configureList, ConfigureDTO.class));
    }

    /**
     * 根据ID查询
     * configure/1
     * @param id id
     * @return response
     */
    @RequestMapping(value = "query/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Response queryByPrimaryKey(@PathVariable int id){
        if(id <= 0){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS, "primary key");
        }
        Configure configure = configureRepository.queryConfigureById(id);
        return new Response(DTOConvert.entity2DTO(configure, ConfigureDTO.class));
    }

    /**
     * configure
     * configure?id=1 ...
     * @return response
     */
    @RequestMapping(value = "update", method = RequestMethod.PATCH)
    @ResponseBody
    public Response update(Configure configure){
        //参数校验
        if(configure == null || configure.getId() <= 0){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        Result result = configureRepository.updateConfigure(configure);
        return Response.resultResponse(result);
    }


    @RequestMapping(value = "add", method = RequestMethod.PUT)
    @ResponseBody
    public Response insert(Configure configure){
        if(configure == null ||
                StringUtils.isBlank(configure.getGroup())||
                StringUtils.isBlank(configure.getKey()) ||
                StringUtils.isBlank(configure.getValue())){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        Result result = configureRepository.insertConfigure(configure);
        return Response.resultResponse(result);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response delete(int primaryKey){
        if(primaryKey <=0 ){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        Result result = configureRepository.deleteConfigureById(primaryKey);
        return Response.resultResponse(result);
    }

    //刷新缓存
    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    @ResponseBody
    public Response refresh(){
        AuthorityUtil.getInstance().clear();
        return Response.SUCCESS;
    }

}
