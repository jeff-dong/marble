package com.github.jxdong.marble.domain.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="dongjianxing@aliyun.com">jeff</a>
 * @version 2015/7/2 14:55
 */
public class LoginAccount extends Entity {

    private String mail;
    private String department;
    private String company;
    private String name;
    private String displayName;
    private List<Map<String, String>> memberOf;
    private String employee;
    private String distinguishedName;
    private String city;
    //权限
    private Set<String> authorities;

    //是否为admin
    private boolean hasAdminRole;

    public boolean getHasAdminRole() {
        return hasAdminRole;
    }

    public void setHasAdminRole(boolean hasAdminRole) {
        this.hasAdminRole = hasAdminRole;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Map<String, String>> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(List<Map<String, String>> memberOf) {
        this.memberOf = memberOf;
    }

    public String getEmployee() {
        return employee;
    }

    public String getUpperEmployee() {
        return employee!=null?employee.toUpperCase():employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }


    @Override
    public String toString() {
        return String.format("name: %s, company:%s, department: %s, employee: %s, isAdmin:%s",
                this.getName(),
                this.getCompany(),
                this.getDepartment(),
                this.getEmployee(),
                this.getHasAdminRole());
    }
}
