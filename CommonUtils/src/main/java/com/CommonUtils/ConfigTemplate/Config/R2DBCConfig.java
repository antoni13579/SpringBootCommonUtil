package com.CommonUtils.ConfigTemplate.Config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.core.DatabaseClient;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import dev.miku.r2dbc.mysql.constant.ZeroDateOption;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.ConnectionFactory;

/**R2DBC为实验中的功能，暂不推荐使用
 * @deprecated
 * */
@Deprecated(since="R2DBC为实验中的功能，暂不推荐使用")
@Configuration
public class R2DBCConfig 
{
	@Bean(name = "mydb2ConnectionFactory")
	public ConnectionFactory mydb2ConnectionFactory()
	{
		return MySqlConnectionFactory.from
		(
				MySqlConnectionConfiguration.builder()
											.connectTimeout(Duration.ofSeconds(4))
											.database("mydb2")
											.host("127.0.0.1")
											.password("root")
											.port(3306)
											.username("root")
											.zeroDateOption(ZeroDateOption.EXCEPTION)
											.build()
		);
	}
	
	@Bean(name = "mydb2ConnectionPool", destroyMethod="close")
	public ConnectionPool mydb2ConnectionPool(@Qualifier("mydb2ConnectionFactory")ConnectionFactory mydb2ConnectionFactory)
	{ return com.CommonUtils.Config.SQL.R2DBC.Config.BaseConfig.getConnectionPool(mydb2ConnectionFactory, "CTT", "mydb2ConnectionPool", "select 1"); }
	
	@Bean(name = "mydb2DatabaseClient")
	public DatabaseClient mydb2DatabaseClient(@Qualifier("mydb2ConnectionPool")ConnectionPool mydb2ConnectionPool)
	{ return DatabaseClient.create(mydb2ConnectionPool.unwrap()); }
	
	@Bean(name = "mydb2R2dbcTransactionManager")
	public R2dbcTransactionManager mydb2R2dbcTransactionManager(@Qualifier("mydb2ConnectionFactory")ConnectionFactory mydb2ConnectionFactory)
	{ return new R2dbcTransactionManager(mydb2ConnectionFactory); }
}