package com.CommonUtils.Jdbc.Bean.DBBaseInfo;

import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public final class DBInfoForDataSource extends AbstractDBInfo 
{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private void init(final DataSource dataSource, 
					  final JdbcTemplate jdbcTemplate, 
					  final String sql, 
					  final List<Object[]> bindingParams,
					  final boolean useColumnName)
	{
		this.dataSource = dataSource;
		this.jdbcTemplate = jdbcTemplate;
		super.sql = sql;
		super.bindingParams = bindingParams;
		super.useColumnName = useColumnName;
	}
	
	public DBInfoForDataSource(final DataSource dataSource, 
							   final String sql, 
							   final List<Object[]> bindingParams,
							   final boolean useColumnName)
	{ init(dataSource, new JdbcTemplate(dataSource), sql, bindingParams, useColumnName); }
	
	public DBInfoForDataSource(final DataSource dataSource, 
			   				   final String sql,
			   				   final boolean useColumnName)
	{ init(dataSource, new JdbcTemplate(dataSource), sql, Collections.emptyList(), useColumnName); }
	
	public DBInfoForDataSource(final JdbcTemplate jdbcTemplate, 
							   final String sql, 
							   final List<Object[]> bindingParams,
							   final boolean useColumnName)
	{ init(jdbcTemplate.getDataSource(), jdbcTemplate, sql, bindingParams, useColumnName); }
	
	public DBInfoForDataSource(final JdbcTemplate jdbcTemplate, 
			   				   final String sql,
			   				   final boolean useColumnName)
	{ init(jdbcTemplate.getDataSource(), jdbcTemplate, sql, Collections.emptyList(), useColumnName); }
}