package com.CommonUtils.Config.Jdbc.DataSources;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory;

import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;

public final class TomcatOldDataSourceConfig
{
	private TomcatOldDataSourceConfig() {}
	
	public static DataSource getDataSource(final DBInfo dbInfo, final String poolName) throws Exception
	{
		Properties properties = new Properties();
		properties.put("name", poolName);
		properties.put("auth", "Container");
		properties.put("type", "javax.sql.DataSource");
		properties.put("driverClassName", dbInfo.getJdbcDriverName());
		properties.put("url", dbInfo.getJdbcUrl());
		properties.put("username", dbInfo.getJdbcUserName());
		properties.put("password", dbInfo.getJdbcPassWord());
		properties.put("initialSize", 2);
		properties.put("maxActive", 40);
		properties.put("maxIdle", 600000);
		properties.put("maxWait", 1800000);
		properties.put("removeAbandoned", true);
		properties.put("removeAbandonedTimeout", 30000);
		properties.put("logAbandoned", true);
		return BasicDataSourceFactory.createDataSource(properties);
	}
}