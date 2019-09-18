package com.CommonUtils.Config.Mybatis.Config;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

public final class SqlSessionFactoryConfig 
{
	private SqlSessionFactoryConfig() {}
	
	public static SqlSessionFactory getInstance(final DataSource dataSource, final String mapperXmlPath) throws Exception
	{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
        bean.setPlugins(PageConfig.getGithubPageInterceptor());
		bean.setConfiguration(MybatisBaseConfig.getConfigurationForMyBatis());
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(getMapperPath(mapperXmlPath)));
		return bean.getObject();
	}
	
	public static SqlSessionFactory getInstance(final DataSource dataSource, final MybatisConfiguration mybatisConfiguration, final String mapperXmlPath) throws Exception
	{		
		MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
		mybatisPlus.setDataSource(dataSource);
		mybatisPlus.setPlugins(new Interceptor[] { PageConfig.getBaomidouPaginationInterceptor() });
		mybatisPlus.setConfiguration(mybatisConfiguration);
		mybatisPlus.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(getMapperPath(mapperXmlPath)));
		return mybatisPlus.getObject();
	}
	
	/**
	 * 生成mapper xml的路径
	 * 如输入com.ExtractDataToTD.MyBatis.ScsPlus.Edw.Dao.Mapper
	 * 则生成classpath*:com/ExtractDataToTD/MyBatis/ScsPlus/Edw/Dao/Mapper/*.xml
	 * */
	private static String getMapperPath(final String mapperPath)
	{
		StringBuilder result = new StringBuilder();
		result.append("classpath*:");
		
		String finalMapperPath = mapperPath;
		result.append(finalMapperPath.replace('.', '/'));
		result.append("/*.xml");
		
		return result.toString();
	}
}