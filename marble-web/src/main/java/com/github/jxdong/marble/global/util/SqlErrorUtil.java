package com.github.jxdong.marble.global.util;

import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

public class SqlErrorUtil {

    private static String getErrorMsgByCode(int errorCode) {
        String errorMsg;

        switch (errorCode) {
            case 1054:
                errorMsg = "SQL语法错误";
                break;
            case 1064:
                errorMsg = "SQL语法错误";
                break;
            case 1146:
                errorMsg = "SQL语法错误";
                break;
            case 1062:
                errorMsg = "数据重复";
                break;
            case 630:
                errorMsg = "数据完整性校验错误";
                break;
            case 839:
                errorMsg = "数据完整性校验错误";
                break;
            case 840:
                errorMsg = "数据完整性校验错误";
                break;
            case 893:
                errorMsg = "数据完整性校验错误";
                break;
            case 1169:
                errorMsg = "数据完整性校验错误";
                break;
            case 1215:
                errorMsg = "数据完整性校验错误";
                break;
            case 1216:
                errorMsg = "数据完整性校验错误";
                break;
            case 1217:
                errorMsg = "数据完整性校验错误";
                break;
            case 1451:
                errorMsg = "数据完整性校验错误";
                break;
            case 1452:
                errorMsg = "数据完整性校验错误";
                break;
            case 1557:
                errorMsg = "数据完整性校验错误";
                break;
            case 1:
                errorMsg = "访问数据失败";
                break;
            case 1205:
                errorMsg = "不能请求锁定的数据";
                break;
            case 1213:
                errorMsg = "死锁";
                break;
            default:
                errorMsg = "未知异常";
                break;
        }

        return errorMsg;
    }

    public static String getDataAccessExceptionMsg(DataAccessException e) {
        String errorMsg = "未知异常";
        if (e != null) {
            SQLException sqle = null;
            try {
                sqle = (SQLException) e.getCause();
                if (sqle != null) {
                    errorMsg = getErrorMsgByCode(sqle.getErrorCode());
                }
            } catch (Exception e1) {}
        }
        return errorMsg;
    }
}
