package com.CommonUtils.Utils.DBUtils.Bean.Url;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public final class MySqlUrl 
{
	private String hostIp;
	private int port;
	private String dataBase;
	private boolean useCursorFetch;
	private int defaultFetchSize;
	private boolean useUnicode;
	private String characterEncoding;
	private boolean useSSL;
	private boolean autoReconnect;
	private boolean allowPublicKeyRetrieval;
	
	/**可以通过java.time.ZoneId.SHORT_IDS获取相关时区*/
	private String serverTimezone;
}