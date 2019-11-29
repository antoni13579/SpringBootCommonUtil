package com.CommonUtils.ConfigTemplate.Config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.DatabaseClient;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import dev.miku.r2dbc.mysql.constant.ZeroDateOption;
import io.r2dbc.pool.ConnectionPool;

/**R2DBC为实验中的功能，暂不推荐使用*/
@Deprecated
@Configuration
public class R2DBCConfig 
{
	@Bean(name = "mydb2ConnectionPool", destroyMethod="close")
	public ConnectionPool mydb2ConnectionPool()
	{
		return com.CommonUtils.Config.R2DBC.SpringConnectionPoolConfig.getInstance
		(
				MySqlConnectionFactory.from
				(
						MySqlConnectionConfiguration
							.builder()
							.connectTimeout(Duration.ofSeconds(4))
							.database("mydb2")
							.host("127.0.0.1")
							.password("root")
							.port(3306)
							.username("root")
							.zeroDateOption(ZeroDateOption.EXCEPTION)
							.build()
				), 
				"CTT", 
				"mydb2ConnectionPool", 
				"select 1"
		);
	}
	
	@Bean(name = "mydb2DatabaseClient")
	public DatabaseClient mydb2DatabaseClient(@Qualifier("mydb2ConnectionPool")ConnectionPool mydb2ConnectionPool)
	{ return DatabaseClient.create(mydb2ConnectionPool.unwrap()); }
}