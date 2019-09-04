package com.CommonUtils.Utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.CommonUtils.Jdbc.Bean.DBBaseInfo.AbstractDBInfo;
import com.CommonUtils.Jdbc.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Jdbc.Bean.DBBaseInfo.DBInfoForDataSource;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CrudUtil 
{
	private CrudUtil() {}
	
	public static void batchWrite(final AbstractDBInfo abstractDBInfo)
	{		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean usePool = false;
		try
		{
			if (abstractDBInfo instanceof DBInfo)
			{
				connection = DBHandleUtil.getConnection(abstractDBInfo);			
				preparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.WRITE, connection, abstractDBInfo.getSql());
				DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.WRITE, preparedStatement, abstractDBInfo.getBindingParams());
				DBHandleUtil.commit(new PreparedStatement[] {preparedStatement}, new Connection[] {connection}, true);
			}
			else if (abstractDBInfo instanceof DBInfoForDataSource)
			{
				DBInfoForDataSource dbInfoForDataSource = (DBInfoForDataSource)abstractDBInfo;
				usePool = true;
				if (!JavaCollectionsUtil.isCollectionEmpty(dbInfoForDataSource.getBindingParams()))
				{
					dbInfoForDataSource.getJdbcTemplate().batchUpdate
					(
							dbInfoForDataSource.getSql(), 
							new BatchPreparedStatementSetter() 
							{
								@Override
								public void setValues(PreparedStatement ps, int indx) throws SQLException 
								{
									Object[] finalRecord = dbInfoForDataSource.getBindingParams().get(indx);
									for (int i = 1; i <= finalRecord.length; i++)
									{ ps.setObject(i, finalRecord[i - 1]); }
								}

								@Override
								public int getBatchSize() 
								{ return dbInfoForDataSource.getBindingParams().size(); }
							}
				    ); 
				}
				else
				{ dbInfoForDataSource.getJdbcTemplate().batchUpdate(dbInfoForDataSource.getSql()); }
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
		}
		catch (Exception ex)
		{
			log.error("对数据库进行DML处理出现异常，异常为：", ex);
			if (!usePool)
			{ DBHandleUtil.rollback(connection); }
		}
		finally
		{ ReleaseItemUtil.releaseRelatedResourcesNoDataSource(new Connection[] {connection}, null, new PreparedStatement[] {preparedStatement}); }
	}
	
	public static List<Map<String, Object>> getRecords(final AbstractDBInfo abstractDBInfo)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Map<String, Object>> result = null;
		try
		{
			if (abstractDBInfo instanceof DBInfo)
			{
				result = new ArrayList<>();
				connection = DBHandleUtil.getConnection(abstractDBInfo);
				preparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, connection, abstractDBInfo.getSql());
				DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, preparedStatement, abstractDBInfo.getBindingParams());
				
				resultSet = preparedStatement.executeQuery();
				ResultSetMetaData resultSetMetaData = preparedStatement.getMetaData();
				while (resultSet.next())
				{
					Map<String, Object> record = new HashMap<String, Object>();
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) 
					{
						String columnName = null;
						if (abstractDBInfo.isUseColumnName())
						{ columnName = resultSetMetaData.getColumnName(i); }
						else
						{ columnName = resultSetMetaData.getColumnLabel(i); }
						
						Object columnValue = resultSet.getObject(columnName);
						record.put(columnName, columnValue);
					}
					
					result.add(record);
				}
			}
			else if (abstractDBInfo instanceof DBInfoForDataSource)
			{
				DBInfoForDataSource dbInfoForDataSource = (DBInfoForDataSource)abstractDBInfo;
				if (!JavaCollectionsUtil.isCollectionEmpty(dbInfoForDataSource.getBindingParams()))
				{ result = dbInfoForDataSource.getJdbcTemplate().queryForList(dbInfoForDataSource.getSql(), dbInfoForDataSource.getBindingParams().get(0)); }
				else
				{ result = dbInfoForDataSource.getJdbcTemplate().queryForList(dbInfoForDataSource.getSql()); }
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
		}
		catch (Exception ex)
		{ log.error("获取数据库记录出现异常，异常为：", ex); }
		finally
		{ ReleaseItemUtil.releaseRelatedResourcesNoDataSource(new Connection[] {connection}, new ResultSet[] {resultSet}, new PreparedStatement[] {preparedStatement}); }
		
		return result;
	}
}