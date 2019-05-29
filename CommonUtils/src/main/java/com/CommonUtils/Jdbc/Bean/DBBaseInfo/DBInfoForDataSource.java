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
	
	private void init(final DataSource dataSource, final JdbcTemplate jdbcTemplate, final String sql, final List<Object[]> bindingParams)
	{
		this.dataSource = dataSource;
		this.jdbcTemplate = jdbcTemplate;
		super.sql = sql;
		super.bindingParams = bindingParams;
	}
	
	public DBInfoForDataSource(final DataSource dataSource, 
							   final String sql, 
							   final List<Object[]> bindingParams)
	{ init(dataSource, new JdbcTemplate(dataSource), sql, bindingParams); }
	
	public DBInfoForDataSource(final DataSource dataSource, 
			   				   final String sql)
	{ init(dataSource, new JdbcTemplate(dataSource), sql, Collections.emptyList()); }
	
	public DBInfoForDataSource(final JdbcTemplate jdbcTemplate, 
							   final String sql, 
							   final List<Object[]> bindingParams)
	{ init(jdbcTemplate.getDataSource(), jdbcTemplate, sql, bindingParams); }
	
	public DBInfoForDataSource(final JdbcTemplate jdbcTemplate, 
			   				   final String sql)
	{ init(jdbcTemplate.getDataSource(), jdbcTemplate, sql, Collections.emptyList()); }
}