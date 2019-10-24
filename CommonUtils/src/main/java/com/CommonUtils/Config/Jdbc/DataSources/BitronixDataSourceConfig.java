package com.CommonUtils.Config.Jdbc.DataSources;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.jta.bitronix.PoolingDataSourceBean;

import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.Properties;

/**测试失败，不能完全实现多数据源事务，暂不清楚原因*/
@Deprecated
public final class BitronixDataSourceConfig 
{
	private BitronixDataSourceConfig() {}
	
	public static DataSource getDataSourceForMySql(final String dataSourceName, 
												   final String validationQuery,
												   final DBInfo dbInfo) throws SQLException
	{
		PoolingDataSourceBean result = generateBitronixDataSourceBean(dataSourceName, validationQuery);
		result.setClassName(DBContants.MySqlJdbcDriver.MYSQL_XA.getJdbcDriver());
		result.setDriverProperties
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
	
	private static PoolingDataSourceBean generateBitronixDataSourceBean(final String dataSourceName,
																		final String validationQuery) throws SQLException
	{
		PoolingDataSourceBean result = new PoolingDataSourceBean();
		//result.addConnectionCustomizer(connectionCustomizer);
		//result.close();
		//result.createPooledConnection(xaFactory, bean)
		//result.findXAResourceHolder(xaResource)
		//result.init();
		//result.removeConnectionCustomizer(connectionCustomizer);
		//result.setAcquireIncrement(acquireIncrement);
		//result.setAcquisitionInterval(acquisitionInterval);
		//result.setAcquisitionTimeout(acquisitionTimeout);
		//result.setAllowLocalTransactions(allowLocalTransactions);
		//result.setApplyTransactionTimeout(applyTransactionTimeout);
		//result.setAutomaticEnlistingEnabled(automaticEnlistingEnabled);
		result.setBeanName(dataSourceName);
		//result.setClassName(className);
		//result.setCursorHoldability(cursorHoldability);
		//result.setDataSource(dataSource);
		//result.setDeferConnectionRelease(deferConnectionRelease);
		//result.setDisabled(disabled);
		//result.setDriverProperties(driverProperties);
		//result.setEnableJdbc4ConnectionTest(enableJdbc4ConnectionTest);
		//result.setFailed(failed);
		//result.setIgnoreRecoveryFailures(ignoreRecoveryFailures);
		//result.setIsolationLevel(isolationLevel);
		//result.setLocalAutoCommit(localAutoCommit);
		
		//result.setLoginTimeout(30);
		
		//result.setLogWriter(out);
		result.setMaxIdleTime(6000);
		result.setMaxPoolSize(40);
		result.setMinPoolSize(2);
		//result.setPreparedStatementCacheSize(preparedStatementCacheSize);
		result.setShareTransactionConnections(true);
		result.setTestQuery(validationQuery);
		//result.setTwoPcOrderingPosition(twoPcOrderingPosition);
		result.setUniqueName(dataSourceName);
		//result.setUseTmJoin(useTmJoin);
		return result;
	}
}