package com.CommonUtils.Config.R2DBC;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.SimplePoolMetricsRecorder;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ValidationDepth;

/**R2DBC为实验中的功能，暂不推荐使用*/
@Deprecated
public final class SpringConnectionPoolConfig 
{
	private SpringConnectionPoolConfig() {}
	
	public static ConnectionPool getInstance(final ConnectionFactory connectionFactory,
											 final String zoneIdKey,
								   			 final String connectionPoolName,
								   			 final String validationQuery)
	{
		return new ConnectionPool
		(
				new ConnectionPoolConfiguration
				(
						connectionFactory,
						Clock.system(ZoneId.of(zoneIdKey, ZoneId.SHORT_IDS)),
						(customizer) -> {},
						2,
						40,
						Duration.ofMillis(600000),
						Duration.ofMillis(3000),
						Duration.ofMillis(1800000),
						Duration.ofMillis(1800000),
						new SimplePoolMetricsRecorder(),
						connectionPoolName,
						true,
						ValidationDepth.REMOTE,
						validationQuery
				)
		);
	}
}