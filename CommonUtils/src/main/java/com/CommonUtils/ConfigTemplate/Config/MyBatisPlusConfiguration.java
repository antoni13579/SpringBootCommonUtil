package com.CommonUtils.ConfigTemplate.Config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.CommonUtils.Config.Mybatis.Config.MybatisBaseConfig;
import com.CommonUtils.Config.Mybatis.Config.SqlSessionFactoryConfig;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashMap;

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