package com.CommonUtils.Config.SQL.Jdbc.Config.DataSources;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;

/**我相对害怕阿里的框架，都是为了KPI而诞生的产物，说不定啥时候又不维护*/
@Deprecated
public final class DruidDataSourceConfig 
{
	private DruidDataSourceConfig() {}
	
	/**
	 * 已过时，Druid与Atomikos集成有问题
	 * */
	public static XADataSource getXADataSource(final DBInfo dbInfo, final String validationQuery)
	{
		DruidXADataSource dataSource = new DruidXADataSource();
		setDataSource(dataSource, dbInfo, validationQuery);
		return dataSource;
	}
	
	public static DataSource getDataSource(final DBInfo dbInfo, final String validationQuery)
	{
		DruidDataSource dataSource = new DruidDataSource();
		setDataSource(dataSource, dbInfo, validationQuery);
		return dataSource;
	}
	
	private static void setDataSource(final DruidDataSource dataSource, final DBInfo dbInfo, final String validationQuery)
	{
		dataSource.setUrl(dbInfo.getJdbcUrl());
		dataSource.setUsername(dbInfo.getJdbcUserName());
		dataSource.setPassword(dbInfo.getJdbcPassWord());
		dataSource.setDriverClassName(dbInfo.getJdbcDriverName());
		
		//初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
		dataSource.setInitialSize(2);
		
		//最大连接池数量
		dataSource.setMaxActive(40);
		
		//最小连接池数量
		dataSource.setMinIdle(2);
		
		//获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
		dataSource.setMaxWait(60000);
		
		/**
		 * 是否缓存preparedStatement，也就是PSCache。
		 * PSCache对支持游标的数据库性能提升巨大，比如说oracle。
		 * 在mysql5.5以下的版本中没有PSCache功能，建议关闭掉。
		 * 5.5及以上版本有PSCache，建议开启。
		 * 
		 * */
		dataSource.setPoolPreparedStatements(true);
		
		/**
		 * 要启用PSCache，必须配置大于0，当大于0时，
		 * poolPreparedStatements自动触发修改为true。
		 * 在Druid中，不会存在Oracle下PSCache占用内存过多的问题，
		 * 可以把这个数值配置大一些，比如说100
		 * */
		dataSource.setMaxOpenPreparedStatements(20);
		
		dataSource.setValidationQuery(validationQuery);
		
		//申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
		dataSource.setTestOnBorrow(false);
		
		//归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
		dataSource.setTestOnReturn(false);
		
		/**
		 * 建议配置为true，不影响性能，并且保证安全性。
		 * 申请连接的时候检测，如果空闲时间大于
		 * timeBetweenEvictionRunsMillis，
		 * 执行validationQuery检测连接是否有效。
		 * */
		dataSource.setTestWhileIdle(true);
		
		/**
		 * 有两个含义：
		 * 1) Destroy线程会检测连接的间隔时间
		 * 2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
		 * */
		dataSource.setTimeBetweenEvictionRunsMillis(60000);
		
		//Destory线程中如果检测到当前连接的最后活跃时间和当前时间的差值大于minEvictableIdleTimeMillis，则关闭当前连接。
		dataSource.setMinEvictableIdleTimeMillis(300000);
		
		//物理连接初始化的时候执行的sql
		//dataSource.setConnectionInitSqls(connectionInitSqls);
		
		//当数据库抛出一些不可恢复的异常时，抛弃连接
		//dataSource.setExceptionSorter(exceptionSorter);
		
		/**
		 * 	属性类型是字符串，通过别名的方式配置扩展插件，
		 * 常用的插件有：
		 * 监控统计用的filter:stat 
		 * 日志用的filter:log4j
		 * 防御sql注入的filter:wall
		 * */
		//dataSource.setFilters(filters);
		
		/**
		 * 类型是List<com.alibaba.druid.filter.Filter>，
		 * 如果同时配置了filters和proxyFilters，
		 * 是组合关系，并非替换关系
		 * */
		//dataSource.setProxyFilters(filters);
		
		//对于建立时间超过removeAbandonedTimeout的连接强制关闭
		//dataSource.setRemoveAbandoned(false);
		
		//指定连接建立多长时间就需要被强制关闭
		//dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		
		//指定发生removeabandoned的时候，是否记录当前线程的堆栈信息到日志中
		//dataSource.setLogAbandoned(logAbandoned);
	}
}