package com.CommonUtils.Utils.DBUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.DBHandleUtil;
import com.CommonUtils.Utils.DBUtils.PreparedStatementOperationType;

import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.AbstractDBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfoForDataSource;
import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.BytesUtils.BytesUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateFormat;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;
import com.CommonUtils.Utils.IOUtils.IOUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.opencsv.CSVReader;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BatchUtil
{
	/**读取数据库数据，经过处理后，写入到文件*/
	@SafeVarargs
	public static boolean batchFlow(final AbstractDBInfo sourceDBInfo, 
								 final File targetFile, 
								 final String delimiter, 
								 final String encode, 
								 final boolean append, 
								 final DateFormat dateFomat,
								 final ItemProcessor<Map<String, Object>> ... itemProcessors)
	{
		Connection sourceConnection = null;
		PreparedStatement sourcePreparedStatement = null;
		ResultSet sourceResultSet = null;
		
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		boolean result = false;
		try
		{
			List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
			sourceConnection = DBHandleUtil.getConnection(sourceDBInfo);
			sourcePreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, sourceConnection, sourceDBInfo.getSql());
			DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, sourcePreparedStatement, sourceDBInfo.getBindingParams());
			
			sourceResultSet = sourcePreparedStatement.executeQuery();
			ResultSetMetaData sourceResultSetMetaData = sourcePreparedStatement.getMetaData();
			
			fos = new FileOutputStream(targetFile, append);
			osw = new OutputStreamWriter(fos, encode);
			bw = new BufferedWriter(osw);
			
			while (sourceResultSet.next())
			{
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
				{
					String columnName = null;
					if (sourceDBInfo.isUseColumnName())
					{ columnName = sourceResultSetMetaData.getColumnName(i); }
					else
					{ columnName = sourceResultSetMetaData.getColumnLabel(i); }
					
					Object columnValue = sourceResultSet.getObject(columnName);
					record.put(columnName, columnValue);
				}
				
				records.add(record);
				
				if (records.size() % DBContants.fetchSize == 0)
				{ processAndWrite(itemProcessors, records, sourceResultSetMetaData, delimiter, bw, dateFomat, sourceDBInfo.isUseColumnName()); }
			}
			processAndWrite(itemProcessors, records, sourceResultSetMetaData, delimiter, bw, dateFomat, sourceDBInfo.isUseColumnName());
			result = true;
		}
		catch (Exception ex)
		{
			log.error("批量数据流处理执行失败，异常原因为：", ex);
			result = false;
		}
		finally
		{
			IOUtil.closeQuietly(fos, osw, bw);
			DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] { sourceConnection }, new ResultSet[] { sourceResultSet }, new PreparedStatement[] { sourcePreparedStatement });
		}
		
		return result;
	}
	
	@SafeVarargs
	public static boolean batchFlow(final File srcFile, final AbstractDBInfo targetDBInfo, final String encode, final ItemProcessor<String[]> ... itemProcessors)
	{ return batchFlow(srcFile, targetDBInfo, encode, -1, itemProcessors); }
	
	@SafeVarargs
	public static boolean batchFlow(final File srcFile, final AbstractDBInfo targetDBInfo, final String encode, final long skipRow, final ItemProcessor<String[]> ... itemProcessors)
	{
		FileInputStream fos = null;
    	BufferedInputStream bis = null;
    	InputStreamReader isr = null;
    	BufferedReader br = null;
    	CSVReader csvReader = null;
    	
    	Connection targetConnection = null;
		PreparedStatement targetPreparedStatement = null;
		JdbcTemplate targetJdbcTemplate = null;
		boolean usePool = false;
    	
		boolean result = false;
		try
		{
			fos = new FileInputStream(srcFile);
    		bis = new BufferedInputStream(fos);
    		isr = new InputStreamReader(bis, encode);
    		br = new BufferedReader(isr);
    		csvReader = new CSVReader(br);
    		
    		if (targetDBInfo instanceof DBInfo)
			{
				targetConnection = DBHandleUtil.getConnection(targetDBInfo);
				targetPreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.WRITE, targetConnection, targetDBInfo.getSql());
			}
			else if (targetDBInfo instanceof DBInfoForDataSource)
			{
				usePool = true;
				DBInfoForDataSource dbInfoForDataSource = (DBInfoForDataSource)targetDBInfo;
				targetJdbcTemplate = dbInfoForDataSource.getJdbcTemplate();
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
    		
    		List<String[]> records = new ArrayList<>();
    		Iterator<String[]> iter = csvReader.iterator();
    		long rows = 0;
    		while (iter.hasNext())
    		{
    			++rows;
    			if (rows == skipRow)
    			{ continue; }
    			
    			String[] record = iter.next();
    			records.add(record);
    			if (records.size() % DBContants.fetchSize == 0)
    			{
    				if (usePool)
					{ processAndWrite(itemProcessors, records, targetJdbcTemplate, targetDBInfo.getSql()); }
					else
					{ processAndWrite(itemProcessors, records, targetPreparedStatement, targetConnection); }
    			}
    		}
    		
    		if (usePool)
			{ processAndWrite(itemProcessors, records, targetJdbcTemplate, targetDBInfo.getSql()); }
			else
			{ processAndWrite(itemProcessors, records, targetPreparedStatement, targetConnection); }
    		
			result = true;
		}
		catch (Exception ex)
		{
			log.error("读取文件文件数据后，对其进去入库出现异常，异常原因为：", ex);
			if (!usePool)
			{ DBHandleUtil.rollback(targetConnection); }
			
			result = false;
		}
		finally
		{
			IOUtil.closeQuietly(csvReader, br, isr, bis, fos);
			DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] {targetConnection}, null, new PreparedStatement[] {targetPreparedStatement});
		}
		
		return result;
	}
	
	@SafeVarargs
	public static boolean batchFlow(final File srcFile, final AbstractDBInfo targetDBInfo, final String delimiter, final String encode, final ItemProcessor<String[]> ... itemProcessors)
	{ return batchFlow(srcFile, targetDBInfo, delimiter, encode, -1, itemProcessors); }
	
	@SafeVarargs
	public static boolean batchFlow(final File srcFile, final AbstractDBInfo targetDBInfo, final String delimiter, final String encode, final long skipRow, final ItemProcessor<String[]> ... itemProcessors)
	{
		InputStream fis = null;
		Reader isr = null;
		BufferedReader br = null;
		
		Connection targetConnection = null;
		PreparedStatement targetPreparedStatement = null;
		JdbcTemplate targetJdbcTemplate = null;
		boolean usePool = false;
		boolean result = false;
		try
		{
			fis = new FileInputStream(srcFile);
			isr = new InputStreamReader(fis, encode);
			br = new BufferedReader(isr);
			
			if (targetDBInfo instanceof DBInfo)
			{
				targetConnection = DBHandleUtil.getConnection(targetDBInfo);
				targetPreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.WRITE, targetConnection, targetDBInfo.getSql());
			}
			else if (targetDBInfo instanceof DBInfoForDataSource)
			{
				usePool = true;
				DBInfoForDataSource dbInfoForDataSource = (DBInfoForDataSource)targetDBInfo;
				targetJdbcTemplate = dbInfoForDataSource.getJdbcTemplate();
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
			
			String line;
			List<String[]> records = new ArrayList<>();
			long rows = 0;
			while (null != (line = br.readLine()))
			{
				++rows;
				if (rows == skipRow)
				{ continue; }
				
				String[] record = StringUtils.splitPreserveAllTokens(line, delimiter);
				records.add(record);
				if (records.size() % DBContants.fetchSize == 0)
				{
					if (usePool)
					{ processAndWrite(itemProcessors, records, targetJdbcTemplate, targetDBInfo.getSql()); }
					else
					{ processAndWrite(itemProcessors, records, targetPreparedStatement, targetConnection); }
				}
			}
			
			if (usePool)
			{ processAndWrite(itemProcessors, records, targetJdbcTemplate, targetDBInfo.getSql()); }
			else
			{ processAndWrite(itemProcessors, records, targetPreparedStatement, targetConnection); }
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("读取文件文件数据后，对其进去入库出现异常，异常原因为：", ex);
			if (!usePool)
			{ DBHandleUtil.rollback(targetConnection); }
			
			result = false;
		}
		finally
		{
			IOUtil.closeQuietly(fis, isr, br);
			DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] {targetConnection}, null, new PreparedStatement[] {targetPreparedStatement});
		}
		
		return result;
	}
	
	@SafeVarargs
	public static boolean batchFlow(final AbstractDBInfo sourceDBInfo, 
								    final KafkaTemplate<Object, Object> kafkaTemplate, 
									final String topic,
									final String key,
									final ItemProcessor<Map<String, Object>> ... itemProcessors)
	{
		Connection sourceConnection = null;
		PreparedStatement sourcePreparedStatement = null;
		ResultSet sourceResultSet = null;
		boolean result = false;
		try
		{
			List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
			
			//源数据库初始化
			sourceConnection = DBHandleUtil.getConnection(sourceDBInfo);
			sourcePreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, sourceConnection, sourceDBInfo.getSql());
			DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, sourcePreparedStatement, sourceDBInfo.getBindingParams());
			sourceResultSet = sourcePreparedStatement.executeQuery();
			ResultSetMetaData sourceResultSetMetaData = sourcePreparedStatement.getMetaData();
			
			//开始处理数据
			while (sourceResultSet.next())
			{
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
				{
					String columnName = null;
					if (sourceDBInfo.isUseColumnName())
					{ columnName = sourceResultSetMetaData.getColumnName(i); }
					else
					{ columnName = sourceResultSetMetaData.getColumnLabel(i); }
					
					Object columnValue = sourceResultSet.getObject(columnName);
					record.put(columnName, columnValue);
				}
				
				records.add(record);
				
				if (records.size() % DBContants.fetchSize == 0)
				{ processAndSend(itemProcessors, records, topic, kafkaTemplate, key); }
			}
			
			processAndSend(itemProcessors, records, topic, kafkaTemplate, key);
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("批量数据流处理执行失败，异常原因为：", ex);			
			result = false;
		}
		finally
		{ DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] {sourceConnection}, new ResultSet[] {sourceResultSet}, new PreparedStatement[] {sourcePreparedStatement}); }
		
		return result;
	}
	
	@SafeVarargs
	public static boolean batchFlow(final AbstractDBInfo sourceDBInfo, 
								 	final AbstractDBInfo targetDBInfo, 
								 	final ItemProcessor<Map<String, Object>> ... itemProcessors)
	{
		Connection sourceConnection = null;
		Connection targetConnection = null;
		
		PreparedStatement sourcePreparedStatement = null;
		PreparedStatement targetPreparedStatement = null;
		
		JdbcTemplate targetJdbcTemplate = null;
		ResultSet sourceResultSet = null;
		boolean usePool = false;
		boolean result = false;
		try
		{
			List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
			
			//源数据库初始化
			sourceConnection = DBHandleUtil.getConnection(sourceDBInfo);
			sourcePreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, sourceConnection, sourceDBInfo.getSql());
			DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, sourcePreparedStatement, sourceDBInfo.getBindingParams());
			sourceResultSet = sourcePreparedStatement.executeQuery();
			ResultSetMetaData sourceResultSetMetaData = sourcePreparedStatement.getMetaData();
			
			//目标数据库初始化
			DBInfoForDataSource targetDBinfoForDataSource = null;
			if (targetDBInfo instanceof DBInfo)
			{
				targetConnection = DBHandleUtil.getConnection(targetDBInfo);
				targetPreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.WRITE, targetConnection, targetDBInfo.getSql());
			}
			else if (targetDBInfo instanceof DBInfoForDataSource)
			{
				targetDBinfoForDataSource = (DBInfoForDataSource)targetDBInfo;
				usePool = true;
				targetJdbcTemplate = targetDBinfoForDataSource.getJdbcTemplate();
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
			
			//开始处理数据
			while (sourceResultSet.next())
			{
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
				{
					String columnName = null;
					if (sourceDBInfo.isUseColumnName())
					{ columnName = sourceResultSetMetaData.getColumnName(i); }
					else
					{ columnName = sourceResultSetMetaData.getColumnLabel(i); }
					
					Object columnValue = sourceResultSet.getObject(columnName);
					
					record.put(columnName, columnValue);
				}
				
				records.add(record);
				
				if (records.size() % DBContants.fetchSize == 0)
				{
					if (usePool)
					{ processAndWrite(itemProcessors, records, sourceResultSetMetaData, targetJdbcTemplate, targetDBinfoForDataSource.getSql(), sourceDBInfo.isUseColumnName()); }
					else
					{ processAndWrite(itemProcessors, records, sourceResultSetMetaData, targetPreparedStatement, targetConnection, sourceDBInfo.isUseColumnName()); }
				}
			}
			
			if (usePool)
			{ processAndWrite(itemProcessors, records, sourceResultSetMetaData, targetJdbcTemplate, targetDBinfoForDataSource.getSql(), sourceDBInfo.isUseColumnName()); }
			else
			{ processAndWrite(itemProcessors, records, sourceResultSetMetaData, targetPreparedStatement, targetConnection, sourceDBInfo.isUseColumnName()); }
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("批量数据流处理执行失败，异常原因为：", ex);
			if (!usePool)
			{ DBHandleUtil.rollback(targetConnection); }
			
			result = false;
		}
		finally
		{ DBHandleUtil.releaseRelatedResourcesNoDataSource(new Connection[] {sourceConnection, targetConnection}, new ResultSet[] {sourceResultSet}, new PreparedStatement[] {sourcePreparedStatement, targetPreparedStatement}); }
		
		return result;
	}
	
	private static void processAndSend(final ItemProcessor<Map<String, Object>>[] itemProcessors, 
			   						   final List<Map<String, Object>> records,
			   						   final String topic,
			   						   final KafkaTemplate<Object, Object> kafkaTemplate,
			   						   final String key)
	{
		Collection<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
		kafkaTemplate.send(topic, key, BytesUtil.toBytes(newRecords));
		records.clear();
	}
	
	private static void processAndWrite(final ItemProcessor<Map<String, Object>>[] itemProcessors, 
										final List<Map<String, Object>> records, 
										final ResultSetMetaData sourceResultSetMetaData, 
										final PreparedStatement targetPreparedStatement, 
										final Connection targetConnection,
										final boolean useColumnName) throws SQLException
	{
		Collection<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
		
		DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.WRITE, targetPreparedStatement, sourceResultSetMetaData, newRecords, useColumnName);
		DBHandleUtil.commit(new PreparedStatement[] {targetPreparedStatement}, new Connection[] {targetConnection}, true);
		
		records.clear();
	}
	
	private static void processAndWrite(final ItemProcessor<Map<String, Object>>[] itemProcessors, 
										final List<Map<String, Object>> records, 
										final ResultSetMetaData sourceResultSetMetaData, 
										final JdbcTemplate targetJdbcTemplate, 
										final String sql,
										final boolean useColumnName) throws SQLException
	{
		List<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
		
		targetJdbcTemplate.batchUpdate
		(
				sql, 
				new BatchPreparedStatementSetter() 
				{
					@Override
					public void setValues(PreparedStatement ps, int indx) throws SQLException 
					{
						Map<String, Object> finalRecord = newRecords.get(indx);
						for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
						{
							String columnName = null;
							if (useColumnName)
							{ columnName = sourceResultSetMetaData.getColumnName(i); }
							else
							{ columnName = sourceResultSetMetaData.getColumnLabel(i); }
							
							Object columnValue = finalRecord.get(columnName);
							ps.setObject(i, columnValue);
						}
					}

					@Override
					public int getBatchSize() 
					{ return newRecords.size(); }
				}
		);
		
		records.clear();
	}
	
	private static void processAndWrite(final ItemProcessor<String[]>[] itemProcessors,
										final List<String[]> records,
										final PreparedStatement targetPreparedStatement, 
										final Connection targetConnection) throws SQLException
	{
		Collection<String[]> newRecords = batchProcess(records, itemProcessors);		
		DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.WRITE, targetPreparedStatement, newRecords);
		DBHandleUtil.commit(new PreparedStatement[] {targetPreparedStatement}, new Connection[] {targetConnection}, true);
		records.clear();
	}
	
	private static void processAndWrite(final ItemProcessor<String[]>[] itemProcessors,
										final List<String[]> records,
										final JdbcTemplate targetJdbcTemplate, 
										final String sql)
	{
		List<String[]> newRecords = batchProcess(records, itemProcessors);
		
		targetJdbcTemplate.batchUpdate
		(
				sql, 
				new BatchPreparedStatementSetter() 
				{
					@Override
					public void setValues(PreparedStatement ps, int indx) throws SQLException 
					{
						String[] finalRecord = newRecords.get(indx);
						for (int i = 1; i <= finalRecord.length; i++)
						{ ps.setObject(i, finalRecord[i - 1]); }
					}

					@Override
					public int getBatchSize() 
					{ return newRecords.size(); }
				}
	    );
		
		records.clear();
	}
	
	private static void processAndWrite(final ItemProcessor<Map<String, Object>>[] itemProcessors, 
										final List<Map<String, Object>> records,
										final ResultSetMetaData sourceResultSetMetaData,
										final String delimiter,
										final BufferedWriter bw,
										final DateFormat dateFomat,
										final boolean useColumnName) throws Exception
	{
		List<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
		Collection<String> lines = JavaCollectionsUtil.getMapValues(newRecords, sourceResultSetMetaData, delimiter, dateFomat, useColumnName);
		if (!JavaCollectionsUtil.isCollectionEmpty(lines))
		{
			for (String line : lines)
			{
				if (!StringUtil.isStrEmpty(line))
				{
					bw.write(line);
					bw.write("\r\n");
				}
			}
			bw.flush();
		}
		records.clear();
	}
	
	@SafeVarargs
	private static <T> List<T> batchProcess(final List<T> records, final ItemProcessor<T> ... itemProcessors)
	{
		if (!ArrayUtil.isArrayEmpty(itemProcessors))
		{
			List<T> newRecords = new ArrayList<T>();
			
			for (int i = 0; i < itemProcessors.length; i++)
			{
				if (i == 0)
				{
					for (T record : records)
					{
						T deepCopyRecords = ObjectUtil.cloneByStream(record);
						T newRecord = itemProcessors[i].process(deepCopyRecords);
						newRecords.add(newRecord);
					}
				}
				else
				{
					Collection<T> tempRecords = new ArrayList<T>();
					
					for (T newRecord : newRecords)
					{
						T deepCopyRecords = ObjectUtil.cloneByStream(newRecord);
						T tempRecord = itemProcessors[i].process(deepCopyRecords);
						tempRecords.add(tempRecord);
					}
					
					newRecords.clear();
					newRecords.addAll(tempRecords);
				}
			}
			
			return newRecords;
		}
		else
		{ return records; }
	}
	
	@FunctionalInterface
	public interface ItemProcessor<T>
	{ T process(final T itemData); }
	
	@SafeVarargs
	public static <T> void pageQueryHandlerBean(final double totalDataCount, 
		  	  									final double pageSize, 
		  	  									final IService<T> service, 
		  	  									final QueryWrapper<T> queryWrapper,
		  	  									final JavaCollectionsUtil.ItemProcessorForCollection<T> ... itemProcessorForCollections)
	{ pageQueryHandlerBean(totalDataCount, pageSize, service.getBaseMapper(), queryWrapper, itemProcessorForCollections); }
	
	@SafeVarargs
	public static <T> void pageQueryHandlerBean(final double totalDataCount, 
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
	public static <T> void pageQueryHandlerBean(final double totalDataCount, 
				 								final double pageSize, 
				 								final IService<T> service, 
				 								final QueryWrapper<T> queryWrapper,
				 								final JavaCollectionsUtil.ItemProcessorForMap<String, Object> ... itemProcessorForMaps)
	{ pageQueryHandlerBean(totalDataCount, pageSize, service.getBaseMapper(), queryWrapper, itemProcessorForMaps); }
	
	@SafeVarargs
	public static <T> void pageQueryHandlerBean(final double totalDataCount, 
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
}