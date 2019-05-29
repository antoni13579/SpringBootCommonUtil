package com.CommonUtils.Jdbc.DataSources;

import javax.sql.DataSource;

import com.CommonUtils.Jdbc.Bean.DBBaseInfo.DBInfo;
import com.zaxxer.hikari.HikariDataSource;

public final class HikariDataSourceConfig 
{
	private HikariDataSourceConfig() {}
	
	public static DataSource getDataSource(final DBInfo dbInfo, final String poolName, final boolean isReadOnly)
	{
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setDriverClassName(dbInfo.getJdbcDriverName());
		hikariDataSource.setJdbcUrl(dbInfo.getJdbcUrl());
		hikariDataSource.setUsername(dbInfo.getJdbcUserName());
		hikariDataSource.setPassword(dbInfo.getJdbcPassWord());
		hikariDataSource.setReadOnly(isReadOnly);
		hikariDataSource.setConnectionTimeout(30000);
		hikariDataSource.setIdleTimeout(600000);
		hikariDataSource.setMaxLifetime(1800000);
		hikariDataSource.setMaximumPoolSize(40);
		hikariDataSource.setMinimumIdle(2);
		hikariDataSource.setPoolName(poolName);
		return hikariDataSource;
	}
}