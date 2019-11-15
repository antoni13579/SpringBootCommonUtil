package com.CommonUtils.ConfigTemplate.Config;

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.CommonUtils.Config.Jdbc.DataSources.AtomikosDataSourceConfig;
import com.CommonUtils.Utils.DBUtils.DBUrlUtil;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.Url.MySqlUrl;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

//@Configuration
public class AtomikosConfig 
{
	//分布式事务管理器
    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public TransactionManager atomikosTransactionManager()
    {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean(name = "txManager")
    public JtaTransactionManager transactionManager(@Qualifier("userTransaction")UserTransaction userTransaction, 
    													 @Qualifier("atomikosTransactionManager")TransactionManager atomikosTransactionManager) throws SystemException
    {
    	JtaTransactionManager result = new JtaTransactionManager(userTransaction, atomikosTransactionManager);
    	
    	//设置这里，是因为使用Spring Batch提示JtaTransactionManager does not support custom isolation levels by default - switch 'allowCustomIsolationLevels' to 'true'
    	result.setAllowCustomIsolationLevels(true);
    	return result;
    }

    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() throws SystemException
    {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }
    
	@Bean(name = "lukabootDataSource", destroyMethod = "destroy")
	@DependsOn({"txManager"})
	public DataSource lukabootDataSource() throws SQLException
	{
		return AtomikosDataSourceConfig.getDataSourceForMySql
		(
				"lukabootDataSource", 
				"select 1", 
				new DBInfo
				(
						DBUrlUtil.getMySqlUrl
						(
								new MySqlUrl()
									.setAllowPublicKeyRetrieval(true)
									.setAutoReconnect(true)
									.setCharacterEncoding("utf8")
									.setDataBase("lukaboot")
									.setDefaultFetchSize(10000)
									.setHostIp("192.168.30.19")
									.setPort(3306)
									.setServerTimezone("Asia/Shanghai")
									.setUseCursorFetch(true)
									.setUseSSL(true)
									.setUseUnicode(true)
						),
						"root",
						"root",
						true
				)				
		);
	}
	
	@Bean(name = "myDataSource", destroyMethod = "destroy")
	@DependsOn({"txManager"})
	public DataSource myDataSource() throws SQLException
	{
		return AtomikosDataSourceConfig.getDataSourceForMySql
		(
				"myDataSource", 
				"select 1", 
				new DBInfo
				(
						DBUrlUtil.getMySqlUrl
						(
								new MySqlUrl()
									.setAllowPublicKeyRetrieval(true)
									.setAutoReconnect(true)
									.setCharacterEncoding("utf8")
									.setDataBase("jeesite")
									.setDefaultFetchSize(10000)
									.setHostIp("127.0.0.1")
									.setPort(3306)
									.setServerTimezone("Asia/Shanghai")
									.setUseCursorFetch(true)
									.setUseSSL(true)
									.setUseUnicode(true)
						),
						"root",
						"123456",
						true
				)
		);
	}
}