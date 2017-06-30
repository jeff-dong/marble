package com.github.jxdong.marble.global.util;

import com.github.jxdong.marble.common.util.CommonUtil;
import com.github.jxdong.marble.domain.model.AppDetail;
import com.github.jxdong.marble.domain.model.Configure;
import com.github.jxdong.marble.domain.model.Account;
import com.github.jxdong.marble.domain.model.enums.ConfigureEnum;
import com.github.jxdong.marble.domain.model.enums.ErrorEnum;
import com.github.jxdong.marble.domain.repositories.AppRepository;
import com.github.jxdong.marble.domain.repositories.ConfigureRepository;
import com.github.jxdong.marble.global.exception.MarbleException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/7/2 14:49
 */
public class AuthorityUtil {
    private static Logger logger = LoggerFactory.getLogger(AuthorityUtil.class);

    public Account getLoginedAccount() {
        Account account = new Account();
        account.setName("超级用户");
        account.setEmployee("A00001");
        account.setMail("100@100.com");
        account.setDisplayName("超级用户");
        account.setDistinguishedName("超级用户");
        account.setHasAdminRole(true);
        return account;
    }

    /**
     * 校验userName是否有authorityCode权限
     *
     * @param appCode 应用Code
     * @return boolean 默认false
     */

    public boolean validateAuthority(String appCode) throws MarbleException {
        Account account = getLoginedAccount();
        if (account == null || StringUtils.isBlank(account.getEmployee())) {
            logger.warn("Validate failed. The param is invalid. employee:{}", account);
            throw new MarbleException(ErrorEnum.ILLEGAL_ARGUMENT, "");
        }
        if (account.getHasAdminRole()) {
            return true;
        }
        String employeeID = account.getUpperEmployee();
        //查找DB中对应的app的owner
        AppRepository appRepository = (AppRepository) SpringContextUtil.getBean("appRepositoryImpl");
        AppDetail app = appRepository.queryAppByCode(appCode);
        if (app == null) {
            logger.warn("There is no app with code({})", appCode);
            throw new MarbleException(ErrorEnum.ILLEGAL_ARGUMENT, "Cannot find the App with code-" + appCode);
        }

        String owners = app.getOwner();
        if (com.github.jxdong.marble.common.util.StringUtils.isNotBlank(owners)) {
            String[] ownerArray = owners.split(";");
            for (String str : ownerArray) {
                if (employeeID.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        throw new MarbleException(ErrorEnum.NO_PERMISSION, "");
    }

    //取得本机IP
    public static String getLocalIPAddress() {
        String ipAddress = "";
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        if (!"127.0.0.1".equals(ip.getHostAddress())) {
                            ipAddress = ip.getHostAddress();
                            logger.info("IP address :{}", ip.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("get local ip address exception. ", e);
        }
        return ipAddress;
    }


    //清空缓存
    public void clear() {
        this.adminList = null;
    }

    //单例模式实现
    private static class SigletonHolder {
        private static final AuthorityUtil instance = new AuthorityUtil();
    }

    public static final AuthorityUtil getInstance() {
        return SigletonHolder.instance;
    }

    private Set<String> adminList;
}
