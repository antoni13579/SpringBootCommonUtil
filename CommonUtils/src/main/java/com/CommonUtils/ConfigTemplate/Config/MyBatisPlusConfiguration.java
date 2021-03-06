package com.CommonUtils.ConfigTemplate.Config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.CommonUtils.Config.SQL.Mybatis.Config.MybatisBaseConfig;
import com.CommonUtils.Config.SQL.Mybatis.Config.SqlSessionFactoryConfig;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationInterceptor;
import com.baomidou.dynamic.datasource.processor.DsHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.mybatisplus.annotation.IdType;

@Configuration
@MapperScan(basePackages = {"com.CommonUtils.ConfigTemplate.MyBatis.**.mapper*"})
public class MyBatisPlusConfiguration 
{
	@Bean(name = "dynamicRoutingDataSource", destroyMethod = "destroy")
	public DynamicRoutingDataSource dynamicRoutingDataSource(@Qualifier("mydb4DataSource")DataSource mydb4DataSource, @Qualifier("mydb3DataSource")DataSource mydb3DataSource)
	{		
		DynamicRoutingDataSource result = new DynamicRoutingDataSource();
		result.setProvider
		(
				() -> 
				{
					Map<String, DataSource> dataSourceMap = new HashMap<>();
					dataSourceMap.put("mydb4DataSource", mydb4DataSource);
					dataSourceMap.put("mydb3DataSource", mydb3DataSource);
					return dataSourceMap;
				}
		);
		result.setPrimary("mydb4DataSource");
		result.setStrict(true);
		return result;
	}
	
	@Bean(name = "dynamicRoutingDataSourceSqlSessionFactory")
    public SqlSessionFactory dynamicRoutingDataSourceSqlSessionFactory(@Qualifier("dynamicRoutingDataSource")DynamicRoutingDataSource dynamicRoutingDataSource) throws Exception
    { return SqlSessionFactoryConfig.getInstance(dynamicRoutingDataSource, MybatisBaseConfig.getConfigurationForMyBatisPlus(IdType.NONE), "com.CommonUtils.ConfigTemplate.MyBatis.**.xml"); }
	
	/**
	 * 可选，这个玩意很少用到
	 * */
	@Bean(name = "dynamicRoutingDataSourceSqlSessionTemplate")
    public SqlSessionTemplate mydb1SqlSessionTemplate(@Qualifier("dynamicRoutingDataSourceSqlSessionFactory")SqlSessionFactory dynamicRoutingDataSourceSqlSessionFactory)
    { return new SqlSessionTemplate(dynamicRoutingDataSourceSqlSessionFactory); }
	
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