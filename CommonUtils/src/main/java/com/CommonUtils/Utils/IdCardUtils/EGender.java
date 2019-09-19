package com.CommonUtils.Utils.IdCardUtils;

import lombok.Getter;
import lombok.ToString;

/**已废弃，请使用cn.hutool.core.util.IdcardUtil*/
@Deprecated
@ToString
@Getter
public enum EGender 
{
	MALE("男"),
	FEMALE("女");
	
	private final String gender;
	
	private EGender(final String gender) 
	{ this.gender = gender; }
}