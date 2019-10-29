package com.CommonUtils.ConfigTemplate.Config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.DatabaseClient;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import dev.miku.r2dbc.mysql.constant.ZeroDateOption;
import io.r2dbc.pool.ConnectionPool;

/**R2DBC为实验中的功能，暂不推荐使用*/
@Deprecated
//@Configuration
public class R2DBCConfig 
{
	@Bean(name = "lukabootConnectionPool", destroyMethod="close")
	public ConnectionPool lukabootConnectionPool()
	{
		return com.CommonUtils.Config.R2DBC.SpringConnectionPoolConfig.getInstance
		(
				MySqlConnectionFactory.from
				(
						MySqlConnectionConfiguration
							.builder()
							.connectTimeout(Duration.ofSeconds(4))
							.database("lukaboot")
							.host("192.168.30.19")
							.password("root")
							.port(3306)
							.username("root")
							.zeroDateOption(ZeroDateOption.EXCEPTION)
							.build()
				), 
				"CTT", 
				"lukabootConnectionPool", 
				"select 1"
		);
	}
	
	@Bean(name = "lukabootDatabaseClient")
	public DatabaseClient lukabootDatabaseClient(@Qualifier("lukabootConnectionPool")ConnectionPool lukabootConnectionPool)
	{ return DatabaseClient.create(lukabootConnectionPool.unwrap()); }
}