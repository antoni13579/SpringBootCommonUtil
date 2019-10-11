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
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.DBHandleUtil;
import com.CommonUtils.Utils.DBUtils.DBHandleUtil.PreparedStatementOperationType;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.AbstractDBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfoForDataSource;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.CustomCollections.HashSet;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Replacer;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.text.csv.CsvParser;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriteConfig;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DBBatchFlowUtil
{
	/**读取数据库数据，经过处理后，写入到文件
	 * 
	 * dateFomat参数建议使用cn.hutool.core.date.DatePattern来提供日期格式
	 * */
	@SafeVarargs
	public static boolean dbToTextFile(final AbstractDBInfo sourceDBInfo, 
								 	   final File targetFile, 
								 	   final String delimiter, 
								 	   final Charset encode, 
								 	   final boolean append, 
								 	   final FastDateFormat dateFomat,
								 	   final Replacer<Map<String, Object>> ... itemProcessors)
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
			char[] lineDelimiter = {CharUtil.CR, CharUtil.LF};
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
				Map<String, Object> record = new LinkedHashMap<String, Object>();
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
				
				if (records.size() % DBContants.fetchSize == 0 || sourceResultSet.isLast())
				{
					Collection<String> lines = new ArrayList<>();
					List<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
					//迭代每一行数据
					for (Map<String, Object> tempRecord : newRecords)
					{
						StringBuilder line = new StringBuilder();
						//根据ResultSetMetaData提供的表结构，获取变量record对应的值
						for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
						{
							String columnName = null;
							if (sourceDBInfo.isUseColumnName())
							{ columnName = sourceResultSetMetaData.getColumnName(i); }
							else
							{ columnName = sourceResultSetMetaData.getColumnLabel(i); }
							
							Object columnValue = tempRecord.get(columnName);
							
							String value = null;
							
							if (columnValue instanceof java.util.Date)
							{ value = DateTime.of(Convert.toDate(columnValue)).toString(dateFomat); }
							else if (columnValue instanceof java.sql.Date)
							{ value = DateTime.of(new java.util.Date(Convert.convert(java.sql.Date.class, columnValue).getTime())).toString(dateFomat); }
							else if (columnValue instanceof java.sql.Timestamp)
							{ value = DateTime.of(new java.util.Date(Convert.convert(java.sql.Timestamp.class, columnValue).getTime())).toString(dateFomat); }
							else if (columnValue instanceof oracle.sql.TIMESTAMP)
							{ value = DateTime.of(new java.util.Date(Convert.convert(oracle.sql.TIMESTAMP.class, columnValue).timestampValue().getTime())).toString(dateFomat); }
							else
							{ value = ObjectUtil.toString(columnValue); }

							line.append(value);
							
							if (i < sourceResultSetMetaData.getColumnCount())
							{ line.append(delimiter); }
						}
						lines.add(line.toString());
					}
					
					if (!CollUtil.isEmpty(lines))
					{
						for (String line : lines)
						{
							if (!StrUtil.isEmptyIfStr(line))
							{
								bw.write(line);
								//bw.write("\r\n");
								bw.write(lineDelimiter);
							}
						}
						bw.flush();
					}
					records.clear();
				}
			}
			result = true;
		}
		catch (Exception ex)
		{
			log.error("批量数据流处理执行失败，异常原因为：", ex);
			result = false;
		}
		finally
		{ DbUtil.close(bw, osw, fos, sourceResultSet, sourcePreparedStatement, sourceConnection); }
		
		return result;
	}
	
	@SafeVarargs
	public static boolean dbToKafka(final AbstractDBInfo sourceDBInfo, 
								    final KafkaTemplate<Object, Object> kafkaTemplate, 
									final String topic,
									final String key,
									final Replacer<Map<String, Object>> ... itemProcessors)
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
				Map<String, Object> record = new LinkedHashMap<String, Object>();
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
				
				if (records.size() % DBContants.fetchSize == 0 || sourceResultSet.isLast())
				{
					kafkaTemplate.send(topic, key, ObjectUtil.serialize(batchProcess(records, itemProcessors)));
					records.clear();
				}
			}
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("批量数据流处理执行失败，异常原因为：", ex);			
			result = false;
		}
		finally
		{ DbUtil.close(sourceResultSet, sourcePreparedStatement, sourceConnection); }
		
		return result;
	}
	
	@SafeVarargs
	public static boolean dbToDb(final AbstractDBInfo sourceDBInfo, 
								 final AbstractDBInfo targetDBInfo, 
								 final Replacer<Map<String, Object>> ... itemProcessors)
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
				Map<String, Object> record = new LinkedHashMap<String, Object>();
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
				
				if (records.size() % DBContants.fetchSize == 0 || sourceResultSet.isLast())
				{
					if (usePool)
					{
						List<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
						targetJdbcTemplate.batchUpdate
						(
								targetDBinfoForDataSource.getSql(), 
								new BatchPreparedStatementSetter() 
								{
									@Override
									public void setValues(PreparedStatement ps, int indx) throws SQLException 
									{
										Map<String, Object> finalRecord = newRecords.get(indx);
										for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
										{
											String columnName = null;
											if (sourceDBInfo.isUseColumnName())
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
					else
					{
						DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.WRITE, targetPreparedStatement, sourceResultSetMetaData, batchProcess(records, itemProcessors), sourceDBInfo.isUseColumnName());
						DBHandleUtil.commit(new PreparedStatement[] {targetPreparedStatement}, new Connection[] {targetConnection}, true);
						records.clear();
					}
				}
			}
			
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
		{ DbUtil.close(sourceResultSet, sourcePreparedStatement, targetPreparedStatement, sourceConnection, targetConnection); }
		
		return result;
	}
	
	/**读取数据库，并写入到EXCEL文件*/
	@SafeVarargs
	public static boolean dbToExcel(final AbstractDBInfo sourceDBInfo, 
		 							final File targetFile,
		 							final String sheetName,
		 							final Replacer<Map<String, Object>> ... itemProcessors)
	{
		Connection sourceConnection = null;
		PreparedStatement sourcePreparedStatement = null;
		ResultSet sourceResultSet = null;
		BigExcelWriter bigExcelWriter = null;
		boolean result = false;
		try
		{
			List<Map<String, Object>> records = new ArrayList<>();
			sourceConnection = DBHandleUtil.getConnection(sourceDBInfo);
			sourcePreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, sourceConnection, sourceDBInfo.getSql());
			DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, sourcePreparedStatement, sourceDBInfo.getBindingParams());
			
			sourceResultSet = sourcePreparedStatement.executeQuery();
			ResultSetMetaData sourceResultSetMetaData = sourcePreparedStatement.getMetaData();
			
			bigExcelWriter = ExcelUtil.getBigWriter(targetFile, sheetName);
			
			while (sourceResultSet.next())
			{
				Map<String, Object> record = new LinkedHashMap<String, Object>();
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
				
				if (records.size() % DBContants.fetchSize == 0 || sourceResultSet.isLast())
				{
					bigExcelWriter.write(batchProcess(records, itemProcessors), true).flush();
					records.clear();
				}
			}
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("读取数据库后，并写入到EXCEL出现异常，异常原因为：", ex);
			result = false;
		}
		finally
		{ DbUtil.close(bigExcelWriter, sourceResultSet, sourcePreparedStatement, sourceConnection); }
		
		return result;
	}
	
	/**读取数据库，并写入到CSV文件*/
	@SafeVarargs
	public static boolean dbToCsv(final AbstractDBInfo sourceDBInfo, 
								  final File targetFile,
								  final Charset encode,
								  final boolean isAppend,
								  final FastDateFormat dateFomat,
								  final CsvWriteConfig csvWriteConfig,
								  final Replacer<Map<String, Object>> ... itemProcessors)
	{
		Connection sourceConnection = null;
		PreparedStatement sourcePreparedStatement = null;
		ResultSet sourceResultSet = null;
		
		CsvWriter csvWriter = null;
		boolean result = false;
		try
		{
			List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
			sourceConnection = DBHandleUtil.getConnection(sourceDBInfo);
			sourcePreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.READ, sourceConnection, sourceDBInfo.getSql());
			DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.READ, sourcePreparedStatement, sourceDBInfo.getBindingParams());
			
			sourceResultSet = sourcePreparedStatement.executeQuery();
			ResultSetMetaData sourceResultSetMetaData = sourcePreparedStatement.getMetaData();
			
			csvWriter = CsvUtil.getWriter(targetFile, encode, isAppend, csvWriteConfig);
			
			while (sourceResultSet.next())
			{
				Map<String, Object> record = new LinkedHashMap<String, Object>();
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
				
				if (records.size() % DBContants.fetchSize == 0 || sourceResultSet.isLast())
				{					
					Collection<String[]> lines = new ArrayList<>();
					List<Map<String, Object>> newRecords = batchProcess(records, itemProcessors);
					for (Map<String, Object> tempRecord : newRecords)
					{
						List<String> line = new ArrayList<>();
						for (int i = 1; i <= sourceResultSetMetaData.getColumnCount(); i++) 
						{
							String columnName = null;
							if (sourceDBInfo.isUseColumnName())
							{ columnName = sourceResultSetMetaData.getColumnName(i); }
							else
							{ columnName = sourceResultSetMetaData.getColumnLabel(i); }
							
							Object columnValue = tempRecord.get(columnName);
							
							String value = null;
							
							if (columnValue instanceof java.util.Date)
							{ value = DateTime.of(Convert.toDate(columnValue)).toString(dateFomat); }
							else if (columnValue instanceof java.sql.Date)
							{ value = DateTime.of(new java.util.Date(Convert.convert(java.sql.Date.class, columnValue).getTime())).toString(dateFomat); }
							else if (columnValue instanceof java.sql.Timestamp)
							{ value = DateTime.of(new java.util.Date(Convert.convert(java.sql.Timestamp.class, columnValue).getTime())).toString(dateFomat); }
							else if (columnValue instanceof oracle.sql.TIMESTAMP)
							{ value = DateTime.of(new java.util.Date(Convert.convert(oracle.sql.TIMESTAMP.class, columnValue).timestampValue().getTime())).toString(dateFomat); }
							else
							{ value = ObjectUtil.toString(columnValue); }
							
							line.add(value);
						}
						lines.add(line.toArray(new String[line.size()]));
					}
					csvWriter.write(lines);
					csvWriter.flush();
					records.clear();
				}
			}
			result = true;
		}
		catch (Exception ex)
		{
			log.error("读取数据库后，写入到CSV文件出现异常，异常原因为：", ex);
			result = false;
		}
		finally
		{ DbUtil.close(csvWriter, sourceResultSet, sourcePreparedStatement, sourceConnection); }
		
		return result;
	}
	
	@SafeVarargs
	public static boolean textFileToDb(final File srcFile, 
									   final AbstractDBInfo targetDBInfo, 
									   final String delimiter, 
									   final Charset encode, 
									   final long skipRow, 
									   final Replacer<String[]> ... itemProcessors)
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
				
				//String[] record = StringUtils.splitPreserveAllTokens(line, delimiter);
				List<String> record = StrSpliter.split(line, delimiter, false, false);
				records.add(record.toArray(new String[record.size()]));
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
		{ DbUtil.close(br, isr, fis, targetPreparedStatement, targetConnection); }
		
		return result;
	}
	
	/**读取EXCEL文件，并写入到数据库*/
	@SafeVarargs
	public static boolean excelToDb(final File srcFile, 
									final AbstractDBInfo targetDBInfo, 
									final int sheetIndx,
									final int skipRow,
									final Replacer<Object[]> ... itemProcessors)
	{
		Connection targetConnection = null;
		PreparedStatement targetPreparedStatement = null;
		JdbcTemplate targetJdbcTemplate = null;		
		Set<Boolean> usePools = new HashSet<Boolean>().getSet();
		boolean result = false;
		try
		{
			if (targetDBInfo instanceof DBInfo)
			{
				targetConnection = DBHandleUtil.getConnection(targetDBInfo);
				targetPreparedStatement = DBHandleUtil.getPreparedStatement(PreparedStatementOperationType.WRITE, targetConnection, targetDBInfo.getSql());
			}
			else if (targetDBInfo instanceof DBInfoForDataSource)
			{
				usePools.add(true);
				DBInfoForDataSource dbInfoForDataSource = (DBInfoForDataSource)targetDBInfo;
				targetJdbcTemplate = dbInfoForDataSource.getJdbcTemplate();
			}
			else
			{ throw new Exception("出现了新的AbstractDBInfo继承子类，请及时处理"); }
			
			List<Object[]> records = new ArrayList<>();
			ExcelUtil.readBySax
			(
					srcFile, 
					sheetIndx, 
					new RowHandlerImpl()
						.setAbstractDBInfo(targetDBInfo)
						.setConnection(targetConnection)
						.setItemProcessors(itemProcessors)
						.setJdbcTemplate(targetJdbcTemplate)
						.setPreparedStatement(targetPreparedStatement)
						.setRecords(records)
						.setSkipRow(skipRow)
						.setUsePools(usePools)
			);
			
			if (JavaCollectionsUtil.getOperationFlowResult(usePools))
			{ processAndWrite(itemProcessors, records, targetJdbcTemplate, targetDBInfo.getSql()); }
			else
			{ processAndWrite(itemProcessors, records, targetPreparedStatement, targetConnection); }
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("读取Excel文件数据后，对其进去入库出现异常，异常原因为：", ex);
			if (!JavaCollectionsUtil.getOperationFlowResult(usePools))
			{ DBHandleUtil.rollback(targetConnection); }
			
			result = false;
		}
		finally
		{ DbUtil.close(targetPreparedStatement, targetConnection); }
		
		return result;
	}
	
	/**读取CSV文件，并写入到数据库*/
	@SafeVarargs
	public static boolean csvToDb(final File srcFile, final AbstractDBInfo targetDBInfo, final Charset encode, final long skipRow, final CsvReadConfig csvReadConfig, final Replacer<String[]> ... itemProcessors)
	{
		FileInputStream fos = null;
    	BufferedInputStream bis = null;
    	InputStreamReader isr = null;
    	BufferedReader br = null;
    	CsvParser csvParser = null;
    	
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
    		csvParser = new CsvParser(br, csvReadConfig);
    		
    		
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
    		long rows = 0;
    		CsvRow csvRow;
    		while ((csvRow = csvParser.nextRow()) != null)
    		{
    			++rows;
    			if (rows == skipRow)
    			{ continue; }

    			String[] record = csvRow.toArray(new String[csvRow.size()]);
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
		{ DbUtil.close(csvParser, br, isr, bis, fos, targetPreparedStatement, targetConnection); }
		
		return result;
	}
	
	private static <T> void processAndWrite(final Replacer<T[]>[] itemProcessors,
											final List<T[]> records,
											final PreparedStatement targetPreparedStatement, 
											final Connection targetConnection) throws SQLException
	{
		DBHandleUtil.setPreparedStatement(PreparedStatementOperationType.WRITE, targetPreparedStatement, batchProcess(records, itemProcessors));
		DBHandleUtil.commit(new PreparedStatement[] {targetPreparedStatement}, new Connection[] {targetConnection}, true);
		records.clear();
	}
	
	private static <T> void processAndWrite(final Replacer<T[]>[] itemProcessors,
											final List<T[]> records,
											final JdbcTemplate targetJdbcTemplate, 
											final String sql)
	{
		List<T[]> newRecords = batchProcess(records, itemProcessors);
		targetJdbcTemplate.batchUpdate
		(
				sql, 
				new BatchPreparedStatementSetter() 
				{					
					@Override
					public void setValues(PreparedStatement ps, int indx) throws SQLException 
					{
						T[] finalRecord = newRecords.get(indx);
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
	
	@SafeVarargs
	private static <T> List<T> batchProcess(final List<T> records, final Replacer<T> ... itemProcessors)
	{
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(itemProcessors))
		{
			List<T> newRecords = new ArrayList<T>();
			
			for (int i = 0; i < itemProcessors.length; i++)
			{
				if (i == 0)
				{
					for (T record : records)
					{
						T deepCopyRecords = ObjectUtil.cloneByStream(record);
						T newRecord = itemProcessors[i].replace(deepCopyRecords);
						newRecords.add(newRecord);
					}
				}
				else
				{
					Collection<T> tempRecords = new ArrayList<T>();
					
					for (T newRecord : newRecords)
					{
						T deepCopyRecords = ObjectUtil.cloneByStream(newRecord);
						T tempRecord = itemProcessors[i].replace(deepCopyRecords);
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
	
	@Accessors(chain = true)
	@Setter
	private static class RowHandlerImpl implements RowHandler
	{
		private int skipRow;
		private List<Object[]> records;
		private Set<Boolean> usePools;
		private Replacer<Object[]>[] itemProcessors;
		private Connection connection;
		private PreparedStatement preparedStatement;
		private JdbcTemplate jdbcTemplate;
		private AbstractDBInfo abstractDBInfo;
		
		@Override
		public void handle(int sheetIndex, int rowIndex, List<Object> rowList) 
		{
			if (rowIndex != this.skipRow)
			{
				this.records.add(rowList.toArray());
				if (this.records.size() % DBContants.fetchSize == 0)
				{
					if (JavaCollectionsUtil.getOperationFlowResult(this.usePools))
					{ processAndWrite(this.itemProcessors, this.records, this.jdbcTemplate, this.abstractDBInfo.getSql()); }
					else
					{
						try 
						{ processAndWrite(this.itemProcessors, this.records, this.preparedStatement, this.connection); } 
						catch (SQLException e) 
						{ log.error("读取Excel文件数据后，使用传统模式写入到数据库出现异常，请注意处理，异常原因为：", e); }
					}
				}
			}
		}
	}
	
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
			List<T> records = baseMapper.selectPage(new Page<T>(pageNo, new Double(pageSize).longValue()), queryWrapper).getRecords();
			JavaCollectionsUtil.collectionProcessor
			(
					records, 
					(final T value, final int indx, final int length) -> 
					{
						JavaCollectionsUtil.collectionProcessor
						(
								CollUtil.newArrayList(itemProcessorForCollections), 
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
			List<Map<String, Object>> records = baseMapper.selectMapsPage(new Page<T>(pageNo, new Double(pageSize).longValue()), queryWrapper).getRecords();
			JavaCollectionsUtil.collectionProcessor
			(
					records, 
					(final String key, final Object value, final int indx) -> 
					{
						JavaCollectionsUtil.collectionProcessor
						(
								CollUtil.newArrayList(itemProcessorForMaps), 
								(final JavaCollectionsUtil.ItemProcessorForMap<String, Object> val, final int inx, final int len) -> 
								{ val.process(key, value, indx); }
						);
					}
			);
		}
	}
}