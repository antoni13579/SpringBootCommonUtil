package com.CommonUtils.Utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.springframework.jdbc.support.JdbcUtils;

public final class ReleaseItemUtil 
{
	private ReleaseItemUtil() {}
	
	/**
	 * 释放相关资源，但不包含数据库连接池
	 * */
	public static void releaseRelatedResourcesNoDataSource(final Connection[] connections, final ResultSet[] resultSets, final PreparedStatement[] preparedStatements)
	{
		DBHandleUtil.resetConnectionsSetting(true, connections);
		if (null != resultSets && resultSets.length > 0) { Arrays.asList(resultSets).forEach(resultSet -> { JdbcUtils.closeResultSet(resultSet); }); }
		if (null != preparedStatements && preparedStatements.length > 0) { Arrays.asList(preparedStatements).forEach(preparedStatement -> { JdbcUtils.closeStatement(preparedStatement); }); }
		if (null != connections && connections.length > 0) { Arrays.asList(connections).forEach(connection -> { JdbcUtils.closeConnection(connection); }); }
	}
}