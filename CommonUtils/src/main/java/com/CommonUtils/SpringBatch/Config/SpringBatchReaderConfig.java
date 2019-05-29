package com.CommonUtils.SpringBatch.Config;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.CommonUtils.Utils.DBUtils.DBContants;

public final class SpringBatchReaderConfig 
{
	private SpringBatchReaderConfig() {}
	
	public static <T> JdbcCursorItemReader<T> getJdbcCursorItemReader(final DataSource dataSource,
																	  final String readerName,
																	  final PreparedStatementSetter preparedStatementSetter,
																	  final RowMapper<T> rowMapper,
																	  final String sql)
	{
		JdbcCursorItemReader<T> result = new JdbcCursorItemReader<T>();
		
		result.setConnectionAutoCommit(false);
		//result.setCurrentItemCount(count);
		result.setDataSource(dataSource);
		//result.setDriverSupportsAbsolute(driverSupportsAbsolute);
		result.setFetchSize(DBContants.fetchSize);
		result.setIgnoreWarnings(false);
		//result.setMaxItemCount(count);
		//result.setMaxRows(maxRows);
		result.setName(readerName);
		result.setPreparedStatementSetter(preparedStatementSetter);
		//result.setQueryTimeout(queryTimeout);
		result.setRowMapper(rowMapper);
		//result.setSaveState(saveState);
		result.setSql(sql);
		//result.setUseSharedExtendedConnection(useSharedExtendedConnection);
		//result.setVerifyCursorPosition(verifyCursorPosition);
		return result;
	}
}