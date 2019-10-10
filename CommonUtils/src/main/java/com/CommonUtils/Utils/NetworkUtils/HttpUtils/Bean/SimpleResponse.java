package com.CommonUtils.Utils.NetworkUtils.HttpUtils.Bean;

import cn.hutool.json.JSONUtil;
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
	{ return JSONUtil.toJsonStr(this); }
}