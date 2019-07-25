package com.CommonUtils.Mybatis.Config;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public final class SqlSessionFactoryConfig 
{
	private SqlSessionFactoryConfig() {}
	
	public static SqlSessionFactory getInstance(final DataSource dataSource, final String mapperXmlPath) throws Exception
	{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
        bean.setPlugins(new Interceptor[] {PageConfig.getInstance()});
		bean.setConfiguration(MybatisBaseConfig.getInstance());
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(getMapperPath(mapperXmlPath)));
		return bean.getObject();
	}
	
	public SqlSessionFactory getInstance(final DataSource dataSource, final String mapperXmlPath, final String configPath) throws Exception
	{
		com.CommonUtils.Mybatis.Custom.SqlSessionFactoryBean bean = new com.CommonUtils.Mybatis.Custom.SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
        bean.setPlugins(new Interceptor[] {PageConfig.getInstance()});
		//bean.setConfiguration(MybatisBaseConfig.getInstance());
        // TODO
        // 加载全局的配置文件，如classpath:sqlMapConfig.xml
        bean.setConfigLocation(new DefaultResourceLoader().getResource("classpath:" + configPath));
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(getMapperPath(mapperXmlPath)));
		return bean.getObject();
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