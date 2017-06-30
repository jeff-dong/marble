package com.github.jxdong.marble.controller;

import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/12/29 13:05
 */
@Controller
@RequestMapping("/dataManage/job")
public class QuartzJobDetailController extends BasicController {

    /**
     * 页面跳转
     * @return view
     */
    @RequestMapping("")
    public ModelAndView pageForward() throws Exception {
        return modelAndView("quartz-job");
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public Response query(String jsonParam) throws Exception {

        return new Response();
    }

    //删除jobs
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ResponseBody
    public Response deleteJobs(String schedName, String jobGroup, String jobName) throws Exception{
        if(StringUtils.isBlank(schedName) || StringUtils.isBlank(jobGroup) || StringUtils.isBlank(jobName)){
            return Response.FAILURE(Response.ResultCodeEnum.INVALID_ARGUMENTS);
        }
        return Response.SUCCESS;
    }
}
