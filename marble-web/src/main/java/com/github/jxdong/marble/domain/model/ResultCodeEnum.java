package com.github.jxdong.marble.domain.model;

/**
 * 返回结果状态enum
 * 结果代码: 0.成功；1.其它错误；2.缺少必要参数；3.参数无效；
 * @author jxdong
 *
 */
public enum ResultCodeEnum {

	SUCCESS(0,"成功"),
	OTHER_ERROR(1,"其它错误"),
	LACK_PARAM(2,"缺少必要参数"),
	INVALID_PARAM(3,"参数无效");

	private int code;
	private String desc;

	ResultCodeEnum(int code, String desc){
		this.code = code;
		this.desc = desc;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
