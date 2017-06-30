package com.github.jxdong.marble.global.exception;

import com.github.jxdong.marble.common.util.StringUtils;
import com.github.jxdong.marble.domain.model.enums.ErrorEnum;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/25 15:46
 */
public class MarbleException extends Exception {
    private static final long serialVersionUID = 1L;
    protected String code;
    protected String message;

    public MarbleException() {
    }

    public static void checkArgument(boolean expression, Object errorMessage) throws MarbleException {
        if(expression) {
            throw new MarbleException(ErrorEnum.ILLEGAL_ARGUMENT, StringUtils.safeString(errorMessage));
        }
    }

    public static void check(boolean expression, ErrorEnum codeEnum) throws MarbleException {
        if(expression) {
            throw new MarbleException(codeEnum.getMessage()+"("+codeEnum.getCode()+")");
        }
    }

    public MarbleException(String message) {
        super(message);
    }

    public MarbleException(Throwable cause) {
        super(cause);
    }

    public MarbleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarbleException(ErrorEnum errorEnum, String messageDetail){
        super(errorEnum.getCode() + ":" +errorEnum.getMessage() + ". "+ StringUtils.safeString(messageDetail));
        this.code = errorEnum.getCode();
        this.message = errorEnum.getMessage() + ". "+ messageDetail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



