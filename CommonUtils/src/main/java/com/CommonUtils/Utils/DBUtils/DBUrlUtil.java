package com.CommonUtils.Utils.DBUtils;

import com.CommonUtils.Jdbc.Bean.Url.MySqlUrl;
import com.CommonUtils.Jdbc.Bean.Url.OracleUrl;
import com.CommonUtils.Jdbc.Bean.Url.PostgreSqlUrl;
import com.CommonUtils.Jdbc.Bean.Url.TeradataUrl;
import com.CommonUtils.Utils.StringUtils.StringUtil;

public final class DBUrlUtil 
{
	private static final String TERADATA_URL = "jdbc:teradata://$HOST_IP/CLIENT_CHARSET=$CLIENT_CHARSET,TMODE=$TMODE,CHARSET=$CHARSET,LOB_SUPPORT=$LOB_SUPPORT,database=$database";
	private static final StringBuilder MYSQL_URL = new StringBuilder();
	private static final String ORACLE_URL = "jdbc:oracle:thin:@";
	private static final String POSTGRESQL_URL = "jdbc:postgresql://$HOST_IP:$PORT/$DATABASE?charSet=$CHARSET";
	
	static
	{
		MYSQL_URL.append("jdbc:mysql://");
		MYSQL_URL.append("$HOST_IP");
		MYSQL_URL.append(":");
		MYSQL_URL.append("$PORT");
		MYSQL_URL.append("/");
		MYSQL_URL.append("$DATA_BASE");
		
		MYSQL_URL.append("?useCursorFetch=");
		MYSQL_URL.append("$USE_CURSOR_FETCH");
		
		MYSQL_URL.append("&defaultFetchSize=");
		MYSQL_URL.append("$DEFAULT_FETCH_SIZE");
		
		MYSQL_URL.append("&useUnicode=");
		MYSQL_URL.append("$USE_UNICODE");
		
		MYSQL_URL.append("&characterEncoding=");
		MYSQL_URL.append("$CHARACTER_ENCODING");
		
		MYSQL_URL.append("&useSSL=");
		MYSQL_URL.append("$USE_SSL");
		
		MYSQL_URL.append("&autoReconnect=");
		MYSQL_URL.append("$AUTO_RECONNECT");
		
		MYSQL_URL.append("&allowPublicKeyRetrieval=");
		MYSQL_URL.append("$ALLOW_PUBLIC_KEY_RETRIEVAL");
	}
	
	public static String getTeradataUrl(final TeradataUrl teradataUrl)
	{
		String result = TERADATA_URL;
		result = result.replaceAll("\\$HOST_IP", teradataUrl.getHostIp());
		result = result.replaceAll("\\$CLIENT_CHARSET", teradataUrl.getClientCharset());
		result = result.replaceAll("\\$TMODE", teradataUrl.getTmode());
		result = result.replaceAll("\\$CHARSET", teradataUrl.getCharset());
		result = result.replaceAll("\\$LOB_SUPPORT", teradataUrl.getLobSupport());
		result = result.replaceAll("\\$database", teradataUrl.getDataBase());
		return result;
	}
	
	public static String getMySqlUrl(final MySqlUrl mySqlUrl)
	{
		String result = MYSQL_URL.toString();
		result = result.replaceAll("\\$HOST_IP", mySqlUrl.getHostIp());
		result = result.replaceAll("\\$PORT", String.valueOf(mySqlUrl.getPort()));
		result = result.replaceAll("\\$DATA_BASE", mySqlUrl.getDataBase());
		result = result.replaceAll("\\$USE_CURSOR_FETCH", Boolean.toString(mySqlUrl.isUseCursorFetch()));
		result = result.replaceAll("\\$DEFAULT_FETCH_SIZE", String.valueOf(mySqlUrl.getDefaultFetchSize()));
		result = result.replaceAll("\\$USE_UNICODE", Boolean.toString(mySqlUrl.isUseUnicode()));
		result = result.replaceAll("\\$CHARACTER_ENCODING", mySqlUrl.getCharacterEncoding());
		result = result.replaceAll("\\$USE_SSL", Boolean.toString(mySqlUrl.isUseSSL()));
		result = result.replaceAll("\\$AUTO_RECONNECT", Boolean.toString(mySqlUrl.isAutoReconnect()));
		result = result.replaceAll("\\$ALLOW_PUBLIC_KEY_RETRIEVAL", Boolean.toString(mySqlUrl.isAllowPublicKeyRetrieval()));
		return result;
	}
	
	public static String getOracleUrl(final OracleUrl oracleUrl)
	{
		StringBuilder result = new StringBuilder();
		result.append(ORACLE_URL);
		if (!StringUtil.isStrEmpty(oracleUrl.getTnsname()))
		{ result.append(oracleUrl.getTnsname()); }
		else
		{
			String hostIp = oracleUrl.getHostIp();
			int port = oracleUrl.getPort();
			String instanceName = oracleUrl.getInstanceName();
			
			result.append(hostIp);
			result.append(":");
			result.append(port);
			result.append(":");
			result.append(instanceName);
		}
		
		return result.toString();
	}
	
	public static String getPostgreSqlUrl(final PostgreSqlUrl postgreSqlUrl)
	{
		String result = POSTGRESQL_URL;
		result = result.replaceAll("\\$HOST_IP", postgreSqlUrl.getHostIp());
		result = result.replaceAll("\\$PORT", Integer.toString(postgreSqlUrl.getPort()));
		result = result.replaceAll("\\$DATABASE", postgreSqlUrl.getDataBase());
		result = result.replaceAll("\\$CHARSET", postgreSqlUrl.getCharSet());
		return result;
	}
}