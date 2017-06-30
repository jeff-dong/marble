package com.github.jxdong.marble.domain.model;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/6/25 18:12
 */
public class Response {

    private String resultCode;
    private String resultMessage;
    private Object result;

    //原样前端返回
    private int draw;
    //总记录数
    private int recordsTotal;
    //过滤后记录数
    private int recordsFiltered;

    public static Response SUCCESS = new Response(ResultCodeEnum.SUCCESS);
    public static Response FAILURE = new Response(ResultCodeEnum.FAILURE);

    public Response(){

    }

    public static Response resultResponse(Result result){
        if(result != null){
            if(result.isSuccess()){
                Response res = Response.SUCCESS;
                if(result.getOtherInfo() != null){
                    res.setResult(result.getOtherInfo());
                }
               return res;
            }else{
                return Response.FAILURE(Response.ResultCodeEnum.FAILURE, result.getResultMsg());
            }
        }
        return Response.FAILURE(ResultCodeEnum.INVALID_ARGUMENTS);
    }

    public Response(String resultCode, String resultMessage, Object result){
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.result = result;
    }

    //默认成功
    public Response(Object result){
        this.resultCode = ResultCodeEnum.SUCCESS.getCode();
        this.resultMessage = ResultCodeEnum.SUCCESS.getDesc();
        this.result = result;
       //this.authorities = AuthorityUtil.getAuthorities();
    }

    //默认成功,带page对象
    public Response(Object result, Page page){
        this.resultCode = ResultCodeEnum.SUCCESS.getCode();
        this.resultMessage = ResultCodeEnum.SUCCESS.getDesc();
        this.result = result;
        if(page != null){
            this.setRecordsTotal(page.getTotalRecord());
            this.setRecordsFiltered(page.getTotalRecord());
            this.setDraw(page.getDraw());
        }
        //this.authorities = AuthorityUtil.getAuthorities();
    }

    public static Response FAILURE(ResultCodeEnum codeEnum){
        Response resp = new Response();
        if(codeEnum != null){
            resp.setResultCode(codeEnum.getCode());
            resp.setResultMessage(codeEnum.getDesc());
        }else{
            resp = new Response(ResultCodeEnum.FAILURE);
        }
        return resp;
    }

    public static Response FAILURE(ResultCodeEnum codeEnum, String errorDetail){
        Response resp = new Response();
        if(codeEnum != null){
            resp.setResultCode(codeEnum.getCode());
            resp.setResultMessage(codeEnum.getDesc() + (StringUtils.isNotBlank(errorDetail)?(": "+errorDetail):""));
        }else{
            resp = new Response(ResultCodeEnum.FAILURE);
        }
        return resp;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    //0000.成功；1xxx.失败xxx
    public enum ResultCodeEnum{

        SUCCESS("0000","成功"),
        FAILURE("1000","失败"),
        INVALID_ARGUMENTS("1010", "参数非法"),
        PERMISSION_DENIED("1020", "权限不足"),
        UNKNOWN_ERROR("1030", "未知错误");

        private String code;
        private String desc;

        ResultCodeEnum(String code, String desc){
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }
}
