package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.domain.model.Configure;
import com.github.jxdong.marble.domain.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/6/26 8:40
 */
@Controller @RequestMapping("/lab")
public class LabController extends BasicController{
    private static Logger logger = LoggerFactory.getLogger(LabController.class);


    @RequestMapping(value = "query",method = RequestMethod.GET)
    @ResponseBody
    public Response queryMultiCondition(String jsonParam){
       logger.info("lab query: "+jsonParam);

        return Response.SUCCESS;
    }


    @RequestMapping(value = "update", method = RequestMethod.PATCH)
    @ResponseBody
    public Response update(Configure configure){
        logger.info("lab update: "+configure);

        return Response.SUCCESS;
    }

    @RequestMapping(value = "add", method = RequestMethod.PUT)
    @ResponseBody
    public Response insert(Configure configure){
        logger.info("lab add: "+configure);

        return Response.SUCCESS;
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response delete(int primaryKey){
        logger.info("lab delete: "+primaryKey);

        return Response.SUCCESS;
    }


}
