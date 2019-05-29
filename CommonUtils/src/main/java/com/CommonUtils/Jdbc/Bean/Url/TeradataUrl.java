package com.CommonUtils.Jdbc.Bean.Url;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public final class TeradataUrl
{
	private String hostIp;
	private String clientCharset;
	private String tmode;
	private String charset;
	private String lobSupport;
	private String dataBase;
}