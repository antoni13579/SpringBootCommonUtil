package com.CommonUtils.ConfigTemplate.Config;

import java.sql.SQLException;
import java.time.ZoneId;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.CommonUtils.Config.SQL.Jdbc.Config.DataSources.AtomikosDataSourceConfig;
import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.DBUrlUtil;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.Url.MySqlUrl;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

@Configuration
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
    												@Qualifier("atomikosTransactionManager")TransactionManager atomikosTransactionManager)
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
    
	@Bean(name = "mydb3DataSource", destroyMethod = "destroy")
	@DependsOn({"txManager"})
	public DataSource mydb3DataSource() throws SQLException
	{
		return AtomikosDataSourceConfig.getDataSourceForMySql
		(
				"mydb3DataSource", 
				"select 1", 
				new DBInfo
				(
						DBUrlUtil.getMySqlUrl
						(
								new MySqlUrl()
									.setAllowPublicKeyRetrieval(true)
									.setAutoReconnect(true)
									.setCharacterEncoding("utf8")
									.setDataBase("mydb3")
									.setDefaultFetchSize(DBContants.IN_SQL_STATEMENT_FOR_MYSQL)
									.setHostIp("localhost")
									.setPort(3306)
									.setServerTimezone(ZoneId.SHORT_IDS.get("CTT"))
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
	
	@Bean(name = "mydb4DataSource", destroyMethod = "destroy")
	@DependsOn({"txManager"})
	public DataSource mydb4DataSource() throws SQLException
	{
		return AtomikosDataSourceConfig.getDataSourceForMySql
		(
				"mydb4DataSource", 
				"select 1", 
				new DBInfo
				(
						DBUrlUtil.getMySqlUrl
						(
								new MySqlUrl()
									.setAllowPublicKeyRetrieval(true)
									.setAutoReconnect(true)
									.setCharacterEncoding("utf8")
									.setDataBase("mydb4")
									.setDefaultFetchSize(DBContants.IN_SQL_STATEMENT_FOR_MYSQL)
									.setHostIp("127.0.0.1")
									.setPort(3306)
									.setServerTimezone(ZoneId.SHORT_IDS.get("CTT"))
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
}