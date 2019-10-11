package com.CommonUtils.Utils.DBUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.JdbcUtils;

import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.AbstractDBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfoForDataSource;
import com.CommonUtils.Utils.DBUtils.Bean.DBTable.Column;
import com.CommonUtils.Utils.DBUtils.Bean.DBTable.Table;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.JdbcType;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DBHandleUtil 
{
	private DBHandleUtil() {}
	
	public static void commit(final PreparedStatement[] preparedStatements, final Connection[] connections, final boolean useBatch) throws SQLException
	{
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(preparedStatements))
		{
			for (PreparedStatement preparedStatement : preparedStatements)
			{
				if (useBatch)
				{ preparedStatement.executeBatch(); }
				else
				{ preparedStatement.executeUpdate(); }
			}
		}
		
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(connections))
		{
			for (Connection connection : connections)
			{ connection.commit(); }
		}
		
		if (useBatch) 
		{
			if (!cn.hutool.core.util.ArrayUtil.isEmpty(preparedStatements))
			{
				for (PreparedStatement preparedStatement : preparedStatements)
				{ preparedStatement.clearBatch(); }
			}
		}
	}
	
	public static void rollback(final Connection ... connections)
	{
		if (null != connections && connections.length > 0)
		{
			for (Connection connection : connections)
			{
				if (null != connection)
				{
					try
					{ connection.rollback(); }
					catch (Exception ex)
					{ log.error("事务回滚出现异常，异常原因为：", ex); }
				}
			}
		}
	}
	
	public static Table getColumnBaseInfo(final AbstractDBInfo sourceDBInfo, final String targetTableName)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Table result = new Table().setTableName(targetTableName)
								  .setColumns(new ArrayList<>());
		try
		{
			connection = DBHandleUtil.getConnection(sourceDBInfo);
			preparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, connection, sourceDBInfo.getSql());
			DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, preparedStatement, sourceDBInfo.getBindingParams());
			ResultSetMetaData resultSetMetaData = preparedStatement.getMetaData();
			
			String[] columnNames = new String[resultSetMetaData.getColumnCount()];
			String[] columnLabels = new String[resultSetMetaData.getColumnCount()];
			MultiKeyMap<String, Column> record = new MultiKeyMap<>();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) 
			{
				columnNames[i - 1] = resultSetMetaData.getColumnName(i);
				columnLabels[i - 1] = resultSetMetaData.getColumnLabel(i);
				record.put
				(
						columnLabels[i - 1], 
						columnNames[i - 1], 
						new Column().setColumnLabel(columnLabels[i - 1])
						  			.setColumnName(columnNames[i - 1])
						  			.setIndx(i - 1)
						  			.setColumnTypeForJdbc(JdbcType.valueOf(resultSetMetaData.getColumnType(i)))
						  			.setColumnTypeNameForJdbc(resultSetMetaData.getColumnTypeName(i))
						  			.setColumnTypeNameForJava(resultSetMetaData.getColumnClassName(i))
				);
			}
			result.getColumns().add(record);
			result.setInsertSqlForColumnLabel(generateInsertSqlWithBindingParams(targetTableName, columnLabels))
				  .setInsertSqlForColumnName(generateInsertSqlWithBindingParams(targetTableName, columnNames));
		}
		catch (Exception ex)
		{ log.error("生成INSERT语句出现异常，异常原因为：", ex); }
		finally
		{ releaseRelatedResourcesNoDataSource(new Connection[] {connection}, null, new PreparedStatement[] {preparedStatement}); }
		
		return result;
	}
	
	public static String generateInsertSqlWithBindingParams(final String tableName, final long columnCount)
	{
		StringBuilder sb = new StringBuilder().append("INSERT INTO ")
											  .append(tableName)
											  .append(" VALUES(");
		
		for (long i = 0; i < columnCount; i++)
		{
			sb.append("?");
			if (i != columnCount - 1) { sb.append(","); }
		}
		
		sb.append(")");
		return sb.toString();
	}
	
	public static String generateInsertSqlWithBindingParams(final String tableName, final String ... columnNames) throws Exception
	{
		if (cn.hutool.core.util.ArrayUtil.isEmpty(columnNames))
		{ throw new Exception("生成INSERT语句，字段名称不能为空！！！"); }
		
		StringBuilder sb = new StringBuilder().append("INSERT INTO ")
											  .append(tableName)
											  .append("(");
		
		JavaCollectionsUtil.collectionProcessor
		(
				CollUtil.newArrayList(columnNames), 
				(final String value, final int indx, final int length) -> 
				{
					sb.append(value);
					if (indx != length - 1) { sb.append(","); }
				}
		);
		sb.append(") VALUES(");
		
		
		JavaCollectionsUtil.collectionProcessor
		(
				CollUtil.newArrayList(columnNames), 
				(final String value, final int indx, final int length) -> 
				{
					sb.append("?");
					if (indx != length - 1) { sb.append(","); }
				}
		);
		sb.append(")");
		
		return sb.toString();
	}
	
	public static Connection getConnection(final AbstractDBInfo abstractDBInfo) throws Exception
	{
		if (null != abstractDBInfo)
		{
			DBInfo dbInfo = null;
			DBInfoForDataSource dbInfoForDataSource = null;
			if (abstractDBInfo instanceof DBInfo)
			{
				dbInfo = (DBInfo)abstractDBInfo;
				Class.forName(dbInfo.getJdbcDriverName());
				Connection connection = DriverManager.getConnection(dbInfo.getJdbcUrl(), dbInfo.getJdbcUserName(), dbInfo.getJdbcPassWord());
				connection.setAutoCommit(false);
				return connection;
			}
			else if (abstractDBInfo instanceof DBInfoForDataSource)
			{
				dbInfoForDataSource = (DBInfoForDataSource)abstractDBInfo;
				Connection connection = dbInfoForDataSource.getDataSource().getConnection();
				connection.setAutoCommit(false);
				return connection;
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
		}
		else
		{ return null; }
	}
	
	public static PreparedStatement getPreparedStatement(final PreparedStatementOperationType preparedStatementOperationType, final Connection connection, final String sql) throws SQLException
	{
		PreparedStatement preparedStatement = null;
		
		switch (preparedStatementOperationType)
		{
			case READ:
				preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				preparedStatement.setFetchSize(DBContants.fetchSize);
				preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
				break;
				
			case WRITE:
				preparedStatement = connection.prepareStatement(sql);
				break;
				
			default:
				throw new SQLException("无法生成PreparedStatement，因PreparedStatement操作类型选择不正确");
		}
		
		return preparedStatement;
	}
	
	public static <T> void setPreparedStatement(final PreparedStatementOperationType preparedStatementOperationType, 
												final PreparedStatement preparedStatement, 
												final Collection<T[]> params) throws SQLException
	{
		if (!CollUtil.isEmpty(params))
		{
			for (T[] param : params)
			{
				if (!cn.hutool.core.util.ArrayUtil.isEmpty(param))
				{
					for (int i = 1; i <= param.length; i++)
					{ preparedStatement.setObject(i, param[i - 1]); }
					
					if (Objects.equals(preparedStatementOperationType.name(), PreparedStatementOperationType.WRITE.name())) 
					{ preparedStatement.addBatch(); }
				}
			}
		}
	}
	
	public static void setPreparedStatement(final PreparedStatementOperationType preparedStatementOperationType, 
											final PreparedStatement preparedStatement, 
											final ResultSetMetaData resultSetMetaData, 
											final Collection<Map<String, Object>> params,
											final boolean useColumnName) throws SQLException
	{
		if (!CollUtil.isEmpty(params))
		{
			for (Map<String, Object> param : params)
			{
				if (!CollUtil.isEmpty(param))
				{
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) 
					{
						String columnName = null;
						if (useColumnName)
						{ columnName = resultSetMetaData.getColumnName(i); }
						else
						{ columnName = resultSetMetaData.getColumnLabel(i); }
						
						Object columnValue = param.get(columnName);
						preparedStatement.setObject(i, columnValue);
					}
					
					if (Objects.equals(preparedStatementOperationType.name(), PreparedStatementOperationType.WRITE.name())) 
					{ preparedStatement.addBatch(); }
				}
			}
		}
	}
	
	public static String getPrimaryKeyForVarchar()
	{
		String prefix = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
		
		//(数据类型)(最小值+Math.random()*(最大值-最小值+1))
		int randomNum = (int)(1 + Math.random() * (99999999 - 1 + 1));
		
		String suffix = StrUtil.padPre(Integer.toString(randomNum), 8, '0');
		
		//StrUtil.padPre()
		
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append(suffix);
		return sb.toString();
	}
	
	/**
	 * 读取文件里面的查询SQL语句，把格式化好的SQL转换为一行SQL语句，可以直接提供给java调用，
	 * 不过写的粗糙，有如下限制
	 * 1、不能有注释
	 * 2、若多条查询SQL，必须有半角分号区分
	 * */
	public static Collection<String> getQuerySql(final File file, final Charset encode)
	{
		if (!FileUtil.isFile(file))
		{ return Collections.emptyList(); }
		
		InputStream fis = null;
		Reader isr = null;
		BufferedReader br = null;
		Collection<String> result = new ArrayList<>();
		try
		{
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, encode);
			br = new BufferedReader(isr);
			
			String line;
			StringBuilder sb = null;
			while (null != (line = br.readLine()))
			{
				if (line.contains(";"))
				{
					result.add(sb.append(line).append(" ").toString());
					sb = null;
				}
				else
				{
					if (null == sb)
					{ sb = new StringBuilder(); }
					
					sb.append(line).append(" ");
				}
			}
		}
		catch (Exception ex)
		{ log.error("读取文件，获取查询SQL出现异常，异常原因为：", ex); }
		finally
		{
			IoUtil.close(br);
			IoUtil.close(isr);
			IoUtil.close(fis);
		}
		
		return result;
	}
	
	/**
	 * 释放相关资源，但不包含数据库连接池，建议使用cn.hutool.db.DbUtil.close
	 * */
	@Deprecated
	public static void releaseRelatedResourcesNoDataSource(final Connection[] connections, final ResultSet[] resultSets, final PreparedStatement[] preparedStatements)
	{		
		CollUtil.newArrayList(resultSets).forEach(resultSet -> { JdbcUtils.closeResultSet(resultSet); });
		CollUtil.newArrayList(preparedStatements).forEach(preparedStatement -> { JdbcUtils.closeStatement(preparedStatement); });
		CollUtil.newArrayList(connections)
				.forEach
				(
						connection -> 
						{
							/**数据库连接还原AutoCommit配置与提交事务*/
							if (null != connection)
							{
								try
								{
									connection.commit();
									connection.setAutoCommit(true);
								}
								catch (Exception ex)
								{ log.error("数据库连接还原AutoCommit配置与提交事务出现异常，异常原因为：", ex); }
							}
							
							JdbcUtils.closeConnection(connection);
						}
				);
	}
	
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
				if (!CollUtil.isEmpty(dbInfoForDataSource.getBindingParams()))
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
		{ DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] {connection}, null, new PreparedStatement[] {preparedStatement}); }
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
				if (!CollUtil.isEmpty(dbInfoForDataSource.getBindingParams()))
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
		{ DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] {connection}, new ResultSet[] {resultSet}, new PreparedStatement[] {preparedStatement}); }
		
		return result;
	}
	
	@ToString
	@Getter
	public enum PreparedStatementOperationType 
	{
		READ("以读取模式配置PreparedStatement"),
		WRITE("以修改模式配置PreparedStatement");
		
		private final String preparedStatementOperationType;
		
		private PreparedStatementOperationType(final String preparedStatementOperationType) 
		{ this.preparedStatementOperationType = preparedStatementOperationType; }
	}
}