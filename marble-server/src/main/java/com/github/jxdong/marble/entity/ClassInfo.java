package com.github.jxdong.marble.entity;

import java.io.Serializable;

public class ClassInfo implements Serializable {
    //class 名称
    private String className;
    //class 全路径
    private String classPath;
    //方法名
    private String methodName;
    //方法参数
    private String mathodParam;

    public ClassInfo() {
    }

    public ClassInfo(String className, String methodName, String mathodParam) {
        this.className = className;
        this.methodName = methodName;
        this.mathodParam = mathodParam;
    }

    public ClassInfo(String className, String classPath, String methodName, String mathodParam) {
        this.className = className;
        this.classPath = classPath;
        this.methodName = methodName;
        this.mathodParam = mathodParam;
    }

    public String getMathodParam() {
        return mathodParam;
    }

    public void setMathodParam(String mathodParam) {
        this.mathodParam = mathodParam;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "className='" + (className==null?"":className) + '\'' +
                ", classPath='" + (classPath==null?"":classPath) + '\'' +
                ", methodName='" + (methodName==null?"":methodName) + '\'' +
                ", mathodParam='" + (mathodParam==null?"":mathodParam) + '\'' +
                '}';
    }
}