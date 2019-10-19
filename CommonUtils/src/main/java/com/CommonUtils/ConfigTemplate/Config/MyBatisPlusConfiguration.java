package com.CommonUtils.ConfigTemplate.Config;

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.ibatis.session.SqlSessionFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.CommonUtils.Config.Jdbc.DataSources.AtomikosDataSourceConfig;
import com.CommonUtils.Config.Mybatis.Config.MybatisBaseConfig;
import com.CommonUtils.Config.Mybatis.Config.SqlSessionFactoryConfig;
import com.CommonUtils.Utils.DBUtils.DBUrlUtil;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.Url.MySqlUrl;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashMap;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationInterceptor;
import com.baomidou.dynamic.datasource.processor.DsHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.mybatisplus.annotation.IdType;

//@Configuration
//@MapperScan(basePackages = {"com.CommonUtils.ConfigTemplate.MyBatis.**.mapper*"})
public class MyBatisPlusConfiguration 
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
    public PlatformTransactionManager transactionManager(@Qualifier("userTransaction")UserTransaction userTransaction, 
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
    
	@Bean(name = "dynamicRoutingDataSource", destroyMethod = "destroy")
	public DynamicRoutingDataSource dynamicRoutingDataSource(@Qualifier("myDataSource")DataSource myDataSource, @Qualifier("lukabootDataSource")DataSource lukabootDataSource)
	{		
		DynamicRoutingDataSource result = new DynamicRoutingDataSource();
		result.setProvider(() -> { return new HashMap<String, DataSource>().put("myDataSource", myDataSource).put("lukabootDataSource", lukabootDataSource).getMap(); });
		result.setPrimary("myDataSource");
		result.setStrict(true);
		return result;
	}
	
	@Bean(name = "mySqlSessionFactory")
    public SqlSessionFactory mySqlSessionFactory(@Qualifier("dynamicRoutingDataSource")DynamicRoutingDataSource dynamicRoutingDataSource) throws Exception
    { return SqlSessionFactoryConfig.getInstance(dynamicRoutingDataSource, MybatisBaseConfig.getConfigurationForMyBatisPlus(IdType.NONE), "com.CommonUtils.ConfigTemplate.MyBatis.**.xml"); }
	
	@Bean
	public JdbcTemplate myJdbcTemplate(@Qualifier("myDataSource")DataSource myDataSource)
	{ return new JdbcTemplate(myDataSource); }
	
	@Bean
	public JdbcTemplate lukabootJdbcTemplate(@Qualifier("lukabootDataSource")DataSource lukabootDataSource)
	{ return new JdbcTemplate(lukabootDataSource); }
	
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
	
	/**主要是参考com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration，用于拦截@DS注解*/
	@Bean(name = "dsProcessor")
	public DsProcessor dsProcessor() 
	{
		DsHeaderProcessor headerProcessor = new DsHeaderProcessor();
	    DsSessionProcessor sessionProcessor = new DsSessionProcessor();
	    DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();
	    headerProcessor.setNextProcessor(sessionProcessor);
	    sessionProcessor.setNextProcessor(spelExpressionProcessor);
	    return headerProcessor;
	}
	
	/**主要是参考com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration，用于拦截@DS注解*/
	@Bean(name = "dynamicDatasourceAnnotationAdvisor")
	public DynamicDataSourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor(@Qualifier("dsProcessor")DsProcessor dsProcessor) 
	{
		DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor();
	    interceptor.setDsProcessor(dsProcessor);
	    DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor);
	    advisor.setOrder(1);
	    return advisor;
	}
}