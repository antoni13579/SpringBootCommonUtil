package com.CommonUtils.Config.Jdbc.DataSources;

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;

import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.Properties;

public final class AtomikosDataSourceConfig 
{
	private AtomikosDataSourceConfig() {}
	
	public static DataSource getDataSourceForMySql(final String dataSourceName, 
										   		   final String validationQuery,
										   		   final DBInfo dbInfo) throws SQLException
	{
		AtomikosDataSourceBean result = generateAtomikosDataSourceBean(dataSourceName, validationQuery);
		result.setXaDataSourceClassName(DBContants.MySqlJdbcDriver.MYSQL_XA.getJdbcDriver());
		result.setXaProperties
		(
				new Properties()
					.setProperty("url", dbInfo.getJdbcUrl())
					.setProperty("user", dbInfo.getJdbcUserName())
					.setProperty("password", dbInfo.getJdbcPassWord())
					.setProperty("pinGlobalTxToPhysicalConnection", String.valueOf(true))
					.getProperties()
		);
		return result;
	}
	
	/**建议使用数据库提供的XADataSource，别自己实现或引用第三方的XADataSource，如Druid*/
	@Deprecated
	public static DataSource getDataSource(final String dataSourceName, 
	   		   							   final String validationQuery,
	   		   							   final XADataSource xaDataSource) throws SQLException
	{
		AtomikosDataSourceBean result = generateAtomikosDataSourceBean(dataSourceName, validationQuery);
		result.setXaDataSource(xaDataSource);
		return result;
	}
	
	private static AtomikosDataSourceBean generateAtomikosDataSourceBean(final String dataSourceName, 
	   		   															 final String validationQuery) throws SQLException
	{
		AtomikosDataSourceBean result = new AtomikosDataSourceBean();
		result.setBeanName(dataSourceName);
		result.setBorrowConnectionTimeout(30);
		result.setConcurrentConnectionValidation(true);
		//result.setDefaultIsolationLevel(defaultIsolationLevel);
		result.setLoginTimeout(30);
		//result.setLogWriter(out);
		result.setMaintenanceInterval(60);
		result.setMaxIdleTime(6000);
		result.setMaxLifetime(30000);
		result.setMaxPoolSize(40);
		result.setMinPoolSize(2);
		result.setPoolSize(40);
		//result.setReapTimeout(reapTimeout);
		result.setTestQuery(validationQuery);
		result.setUniqueResourceName(dataSourceName);
		return result;
	}
}