package com.CommonUtils.Utils.IdCardUtils;

import lombok.Getter;
import lombok.ToString;

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