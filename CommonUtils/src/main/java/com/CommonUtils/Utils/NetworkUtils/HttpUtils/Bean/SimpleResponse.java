package com.CommonUtils.Utils.NetworkUtils.HttpUtils.Bean;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SimpleResponse 
{
	private int status;
	private String statusDesc;
	private Object object;
	
	public String toJson()
	{ return JSON.toJSONString(this); }
}