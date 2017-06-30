package com.github.jxdong.marble.agent.entity;

import java.io.Serializable;

/**
 * 0000未成功，其它为失败
 */
public enum ResultCodeEnum implements Serializable {

	SUCCESS("0000","成功"),
	INVALID_ARGUMENTS("1001","参数错误"),
	NO_PERMISSION("1002","没有权限"),
	OTHER_ERROR("1111","其它错误");

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