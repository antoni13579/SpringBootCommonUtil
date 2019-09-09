package com.CommonUtils.Config.Mybatis.Config;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

public final class SqlSessionFactoryConfig 
{
	private SqlSessionFactoryConfig() {}
	
	public static SqlSessionFactory getInstance(final DataSource dataSource, final String mapperXmlPath) throws Exception
	{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
        bean.setPlugins(new Interceptor[] {PageConfig.getGithubPageInterceptor()});
		bean.setConfiguration(MybatisBaseConfig.getConfigurationForMyBatis());
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(getMapperPath(mapperXmlPath)));
		return bean.getObject();
	}
	
	public static SqlSessionFactory getInstance(final DataSource dataSource, final String mapperXmlPath, final String configPath) throws Exception
	{
		com.CommonUtils.Config.Mybatis.Custom.SqlSessionFactoryBean bean = new com.CommonUtils.Config.Mybatis.Custom.SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
        bean.setPlugins(new Interceptor[] {PageConfig.getGithubPageInterceptor()});
		//bean.setConfiguration(MybatisBaseConfig.getInstance());
        // TODO
        // 加载全局的配置文件，如classpath:sqlMapConfig.xml
        bean.setConfigLocation(new DefaultResourceLoader().getResource("classpath:" + configPath));
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(getMapperPath(mapperXmlPath)));
		return bean.getObject();
	}
	
	/*
	@SafeVarargs
	public static MybatisSqlSessionFactoryBean getInstance(final Class<? extends Log> ... logs)
	{
		MybatisConfiguration mybatisConfiguration = MybatisBaseConfig.getConfigurationForMyBatisPlus(logs);
		mybatisConfiguration.setGlobalConfig(new GlobalConfig().setBanner(false).setDbConfig(dbConfig));
		
		MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
		mybatisPlus.setCache(cache);
		//mybatisPlus.setConfigLocation(configLocation);
		mybatisPlus.setConfiguration(MybatisBaseConfig.getInstance(logs));
		mybatisPlus.setConfigurationProperties(sqlSessionFactoryProperties);
		mybatisPlus.setDatabaseIdProvider(databaseIdProvider);
		mybatisPlus.setDataSource(dataSource);
		mybatisPlus.setEnvironment(environment);
		mybatisPlus.setFailFast(failFast);
		mybatisPlus.setGlobalConfig(globalConfig);
		mybatisPlus.setMapperLocations(mapperLocations);
		mybatisPlus.setObjectFactory(objectFactory);
		mybatisPlus.setObjectWrapperFactory(objectWrapperFactory);
		mybatisPlus.setPlugins(plugins);
		mybatisPlus.setSqlSessionFactoryBuilder(sqlSessionFactoryBuilder);
		mybatisPlus.setTransactionFactory(transactionFactory);
		mybatisPlus.setTypeAliases(typeAliases);
		mybatisPlus.setTypeAliasesPackage(typeAliasesPackage);
		mybatisPlus.setTypeAliasesSuperType(typeAliasesSuperType);
		mybatisPlus.setTypeEnumsPackage(typeEnumsPackage);
		mybatisPlus.setTypeHandlers(typeHandlers);
		mybatisPlus.setTypeHandlersPackage(typeHandlersPackage);
		mybatisPlus.setVfs(vfs);
		return mybatisPlus;
	}
	*/
	
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