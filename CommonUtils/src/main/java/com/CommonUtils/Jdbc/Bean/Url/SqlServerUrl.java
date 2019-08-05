package com.CommonUtils.Jdbc.Bean.Url;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public final class SqlServerUrl 
{
	private String hostIp;
	private int port;
	private String dataBase;
}