package com.CommonUtils.Config.SQL.R2DBC.Config;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.SimplePoolMetricsRecorder;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ValidationDepth;


/**
 * R2DBC为实验中的功能，暂不推荐使用
 * @deprecated
 * */
@Deprecated(since="R2DBC为实验中的功能，暂不推荐使用")
public final class BaseConfig 
{
	private BaseConfig() {}
	
	public static ConnectionPool getConnectionPool(final ConnectionFactory connectionFactory,
											 	   final String zoneIdKey,
											 	   final String connectionPoolName,
											 	   final String validationQuery)
	{
		return new ConnectionPool
		(
				ConnectionPoolConfiguration.builder(connectionFactory)
										   .acquireRetry(3)
										   .clock(Clock.system(ZoneId.of(zoneIdKey, ZoneId.SHORT_IDS)))
										   //.customizer((customizer) -> {})
										   .initialSize(2)
										   .maxSize(40)
										   .maxIdleTime(Duration.ofMillis(600000))
										   .maxCreateConnectionTime(Duration.ofMillis(3000))
										   .maxAcquireTime(Duration.ofMillis(1800000))
										   .maxLifeTime(Duration.ofMillis(1800000))
										   .metricsRecorder(new SimplePoolMetricsRecorder())
										   .name(connectionPoolName)
										   .registerJmx(true)
										   .validationDepth(ValidationDepth.REMOTE)
										   .validationQuery(validationQuery)
										   .build()
		);
	}
}