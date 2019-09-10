package com.CommonUtils.Utils.DBUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.jdbc.support.JdbcUtils;

import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.AbstractDBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfoForDataSource;
import com.CommonUtils.Utils.DBUtils.Bean.DBTable.Column;
import com.CommonUtils.Utils.DBUtils.Bean.DBTable.Table;
import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateContants;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;
import com.CommonUtils.Utils.IOUtils.FileUtil;
import com.CommonUtils.Utils.IOUtils.IOUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DBHandleUtil 
{
	private DBHandleUtil() {}
	
	/**数据库连接还原AutoCommit配置与提交事务*/
	public static void resetConnectionsSetting(final boolean commit, final Connection ... connections)
	{
		if (null != connections && connections.length > 0)
		{
			for (Connection connection : connections)
			{
				if (null != connection)
				{
					try
					{
						if (commit) 
						{
							connection.commit();
							connection.setAutoCommit(true);
						}
					}
					catch (Exception ex)
					{ log.error("数据库连接还原AutoCommit配置与提交事务出现异常，异常原因为：", ex); }
				}
			}
		}
	}
	
	public static void commit(final PreparedStatement[] preparedStatements, final Connection[] connections, final boolean useBatch) throws SQLException
	{
		if (!ArrayUtil.isArrayEmpty(preparedStatements))
		{
			for (PreparedStatement preparedStatement : preparedStatements)
			{
				if (useBatch)
				{ preparedStatement.executeBatch(); }
				else
				{ preparedStatement.executeUpdate(); }
			}
		}
		
		if (!ArrayUtil.isArrayEmpty(connections))
		{
			for (Connection connection : connections)
			{ connection.commit(); }
		}
		
		if (useBatch) 
		{
			if (!ArrayUtil.isArrayEmpty(preparedStatements))
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
						  			.setColumnTypeForJdbc(resultSetMetaData.getColumnType(i))
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
		if (ArrayUtil.isArrayEmpty(columnNames))
		{ throw new Exception("生成INSERT语句，字段名称不能为空！！！"); }
		
		StringBuilder sb = new StringBuilder().append("INSERT INTO ")
											  .append(tableName)
											  .append("(");
		ArrayUtil.arrayProcessor
		(
				columnNames, 
				(final String value, final int indx, final int length) -> 
				{
					sb.append(value);
					if (indx != length - 1) { sb.append(","); }
				}
		);
		sb.append(") VALUES(");
		
		ArrayUtil.arrayProcessor
		(
				columnNames,
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
		if (Objects.equals(preparedStatementOperationType.name(), PreparedStatementOperationType.READ.name()))
		{
			preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setFetchSize(DBContants.fetchSize);
			preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
		}
		else if (Objects.equals(preparedStatementOperationType.name(), PreparedStatementOperationType.WRITE.name()))
		{ preparedStatement = connection.prepareStatement(sql); }
		else
		{ throw new SQLException("无法生成PreparedStatement，因PreparedStatement操作类型选择不正确"); }
		return preparedStatement;
	}
	
	public static <T> void setPreparedStatement(final PreparedStatementOperationType preparedStatementOperationType, 
												final PreparedStatement preparedStatement, 
												final Collection<T[]> params) throws SQLException
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(params))
		{
			for (T[] param : params)
			{
				if (!ArrayUtil.isArrayEmpty(param))
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
		if (!JavaCollectionsUtil.isCollectionEmpty(params))
		{
			for (Map<String, Object> param : params)
			{
				if (!JavaCollectionsUtil.isMapEmpty(param))
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
		String prefix = DateUtil.formatDateToStr(new Date(), DateContants.DATE_FORMAT_4);
		
		//(数据类型)(最小值+Math.random()*(最大值-最小值+1))
		int randomNum = (int)(1 + Math.random() * (99999999 - 1 + 1));
		
		String suffix = StringUtil.lpad(Integer.toString(randomNum), 8, '0');
		
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append(suffix);
		return sb.toString();
	}
	
	public static <T, R> List<R> getPrimaryKey(final Function<? super T, ? extends R> mapper, final Collection<T> beans)
	{
		if (!JavaCollectionsUtil.isCollectionEmpty(beans))
		{ return beans.stream().map(mapper).collect(Collectors.toList()); }
		else
		{ return Collections.emptyList(); }
	}

	@SafeVarargs
	public static <R> List<R> getPrimaryKey(final Function<Map<String, Object>, ? extends R> mapper, final Map<String, Object> ... beans)
	{
		if (!ArrayUtil.isArrayEmpty(beans))
		{ return getPrimaryKey(mapper, Arrays.asList(beans)); }
		else
		{ return Collections.emptyList(); }
	}
	
	@SafeVarargs
	public static <T, R> List<R> getPrimaryKey(final Function<? super T, ? extends R> mapper, final T ... beans)
	{
		if (!ArrayUtil.isArrayEmpty(beans))
		{ return getPrimaryKey(mapper, Arrays.asList(beans)); }
		else
		{ return Collections.emptyList(); }
	}
	
	/**
	 * 读取文件里面的查询SQL语句，把格式化好的SQL转换为一行SQL语句，可以直接提供给java调用，
	 * 不过写的粗糙，有如下限制
	 * 1、不能有注释
	 * 2、若多条查询SQL，必须有半角分号区分
	 * */
	public static Collection<String> getQuerySql(final File file, final String encode)
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
		{ IOUtil.closeQuietly(fis, isr, br); }
		
		return result;
	}
	
	@SafeVarargs
	public static <T> void pageQueryHandlerBeanForMybatisPlus(final double totalDataCount, 
		  	  												  final double pageSize, 
		  	  												  final IService<T> service, 
		  	  												  final QueryWrapper<T> queryWrapper,
		  	  												  final JavaCollectionsUtil.ItemProcessorForCollection<T> ... itemProcessorForCollections)
	{ pageQueryHandlerBeanForMybatisPlus(totalDataCount, pageSize, service.getBaseMapper(), queryWrapper, itemProcessorForCollections); }
	
	@SafeVarargs
	public static <T> void pageQueryHandlerBeanForMybatisPlus(final double totalDataCount, 
														  	  final double pageSize, 
														  	  final BaseMapper<T> baseMapper, 
														  	  final QueryWrapper<T> queryWrapper,
														  	  final JavaCollectionsUtil.ItemProcessorForCollection<T> ... itemProcessorForCollections)
	{		
		double totalPage = Math.ceil((totalDataCount + pageSize - 1) / pageSize);
		for (long pageNo = 1; pageNo < totalPage; pageNo++)
		{
			Page<T> page = new Page<T>(pageNo, new Double(pageSize).longValue());
			List<T> records = baseMapper.selectPage(page, queryWrapper).getRecords();
			JavaCollectionsUtil.collectionProcessor
			(
					records, 
					(final T value, final int indx, final int length) -> 
					{
						ArrayUtil.arrayProcessor
						(
								itemProcessorForCollections, 
								(final JavaCollectionsUtil.ItemProcessorForCollection<T> val, final int inx, final int len) -> 
								{ val.process(value, indx, length); }
						);
					}
			);
		}
	}
	
	@SafeVarargs
	public static <T> void pageQueryHandlerMapForMybatisPlus(final double totalDataCount, 
				 											 final double pageSize, 
				 											 final IService<T> service, 
				 											 final QueryWrapper<T> queryWrapper,
				 											 final JavaCollectionsUtil.ItemProcessorForMap<String, Object> ... itemProcessorForMaps)
	{ pageQueryHandlerMapForMybatisPlus(totalDataCount, pageSize, service.getBaseMapper(), queryWrapper, itemProcessorForMaps); }
	
	@SafeVarargs
	public static <T> void pageQueryHandlerMapForMybatisPlus(final double totalDataCount, 
		  	  												 final double pageSize, 
		  	  												 final BaseMapper<T> baseMapper, 
		  	  												 final QueryWrapper<T> queryWrapper,
		  	  												 final JavaCollectionsUtil.ItemProcessorForMap<String, Object> ... itemProcessorForMaps)
	{
		double totalPage = Math.ceil((totalDataCount + pageSize - 1) / pageSize);
		for (long pageNo = 1; pageNo < totalPage; pageNo++)
		{
			Page<T> page = new Page<T>(pageNo, new Double(pageSize).longValue());
			List<Map<String, Object>> records = baseMapper.selectMapsPage(page, queryWrapper).getRecords();
			JavaCollectionsUtil.collectionProcessor
			(
					records, 
					(final String key, final Object value, final int indx) -> 
					{
						ArrayUtil.arrayProcessor
						(
								itemProcessorForMaps, 
								(final JavaCollectionsUtil.ItemProcessorForMap<String, Object> val, final int inx, final int len) -> 
								{ val.process(key, value, indx); }
						);
					}
			);
		}
	}
	
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