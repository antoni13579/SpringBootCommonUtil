package com.CommonUtils.Config.SQL.Jdbc.Config.DataSources;

import javax.sql.DataSource;

import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.zaxxer.hikari.HikariDataSource;

public final class HikariDataSourceConfig 
{
	private HikariDataSourceConfig() {}
	
	public static DataSource getDataSource(final DBInfo dbInfo, final String poolName, final boolean isReadOnly)
	{
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setDriverClassName(dbInfo.getJdbcDriverName());
		hikariDataSource.setJdbcUrl(dbInfo.getJdbcUrl());
		hikariDataSource.setUsername(dbInfo.getJdbcUserName());
		hikariDataSource.setPassword(dbInfo.getJdbcPassWord());
		hikariDataSource.setReadOnly(isReadOnly);
		
		/**
		 * 连接超时时间。默认值为30s，可以接收的最小超时时间为250ms。
		 * 但是连接池请求也可以自定义超时时间（com.zaxxer.hikari.pool.HikariPool#getConnection(long)）。
		 * */
		hikariDataSource.setConnectionTimeout(30000);
		
		/**
		 * 空闲时间。仅在minimum-idle小于maximum-poop-size的时候才会起作用。默认值10分钟。根据应用实际情况做调整，对于一些间歇性流量达到峰值的应用，
		 * 一般需要考虑设置的比间歇时间更大，防止创建数据库连接拖慢了应用速度。
		 * */
		hikariDataSource.setIdleTimeout(600000);
		
		/**
		 * 连接池中连接的最大生命周期。当连接一致处于闲置状态时，数据库可能会主动断开连接。为了防止大量的同一时间处于空闲连接因为数据库方的闲置超时策略断开连接（可以理解为连接雪崩），
		 * 一般将这个值设置的比数据库的“闲置超时时间”小几秒，以便这些连接断开后，HikariCP能迅速的创建新一轮的连接。
		 * */
		hikariDataSource.setMaxLifetime(1800000);
		
		/**
		 * 池中最大连接数（包括空闲和正在使用的连接）。默认值是10，这个一般预估应用的最大连接数，后期根据监测得到一个最大值的一个平均值。要知道，最大连接并不是越多越好，
		 * 一个connection会占用系统的带宽和存储。但是 当连接池没有空闲连接并且已经到达最大值，新来的连接池请求（HikariPool#getConnection）
		 * 会被阻塞直到connectionTimeout（毫秒），超时后便抛出SQLException。
		 * */
		hikariDataSource.setMaximumPoolSize(40);
		
		/**
		 * 池中最小空闲连接数量。默认值10，小于池中最大连接数，一般根据系统大部分情况下的数据库连接情况取一个平均值。Hikari会尽可能、尽快地将空闲连接数维持在这个数量上。
		 * 如果为了获得最佳性能和对峰值需求的响应能力，我们也不妨让他和最大连接数保持一致，使得HikariCP成为一个固定大小的数据库连接池。
		 * */
		hikariDataSource.setMinimumIdle(2);
		
		/**
		 * 连接池的名字。一般会出现在日志和JMX控制台中。默认值：auto-genenrated。建议取一个合适的名字，便于监控。
		 * */
		hikariDataSource.setPoolName(poolName);
		
		/**
		 * 是否自动提交池中返回的连接。默认值为true。一般是有必要自动提交上一个连接中的事物的。如果为false，那么就需要应用层手动提交事物。
		 * */
		//hikariDataSource.setAutoCommit(isAutoCommit);
		return hikariDataSource;
	}
}