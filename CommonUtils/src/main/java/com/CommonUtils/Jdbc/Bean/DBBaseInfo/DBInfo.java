package com.CommonUtils.Jdbc.Bean.DBBaseInfo;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public final class DBInfo extends AbstractDBInfo 
{
	private String jdbcDriverName;
	private String jdbcUrl;
	private String jdbcUserName;
	private String jdbcPassWord;
	
	private void init(final String jdbcDriverName, 
			  		  final String jdbcUrl, 
			  		  final String jdbcUserName, 
			  		  final String jdbcPassWord, 
			  		  final String sql, 
			  		  final List<Object[]> bindingParams,
			  		  final boolean useColumnName)
	{
		this.jdbcDriverName = jdbcDriverName;
		this.jdbcUrl = jdbcUrl;
		this.jdbcUserName = jdbcUserName;
		this.jdbcPassWord = jdbcPassWord;
		super.sql = sql;
		super.bindingParams = bindingParams;
		super.useColumnName = useColumnName;
	}
	
	public DBInfo(final String jdbcDriverName, 
				  final String jdbcUrl, 
				  final String jdbcUserName, 
				  final String jdbcPassWord, 
				  final String sql, 
				  final List<Object[]> bindingParams,
				  final boolean useColumnName)
	{ init(jdbcDriverName, jdbcUrl, jdbcUserName, jdbcPassWord, sql, bindingParams, useColumnName); }
	
	public DBInfo(final String jdbcDriverName, 
			  	  final String jdbcUrl, 
			  	  final String jdbcUserName, 
			  	  final String jdbcPassWord, 
			  	  final String sql,
			  	  final boolean useColumnName)
	{ init(jdbcDriverName, jdbcUrl, jdbcUserName, jdbcPassWord, sql, Collections.emptyList(), useColumnName); }
	
	public DBInfo(final String jdbcDriverName, 
			  	  final String jdbcUrl, 
			  	  final String jdbcUserName, 
			  	  final String jdbcPassWord,
			  	  final boolean useColumnName)
	{ init(jdbcDriverName, jdbcUrl, jdbcUserName, jdbcPassWord, "", Collections.emptyList(), useColumnName); }
}