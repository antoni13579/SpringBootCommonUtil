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
			  		  final List<Object[]> bindingParams)
	{
		this.jdbcDriverName = jdbcDriverName;
		this.jdbcUrl = jdbcUrl;
		this.jdbcUserName = jdbcUserName;
		this.jdbcPassWord = jdbcPassWord;
		super.sql = sql;
		super.bindingParams = bindingParams;
	}
	
	public DBInfo(final String jdbcDriverName, 
				  final String jdbcUrl, 
				  final String jdbcUserName, 
				  final String jdbcPassWord, 
				  final String sql, 
				  final List<Object[]> bindingParams)
	{ init(jdbcDriverName, jdbcUrl, jdbcUserName, jdbcPassWord, sql, bindingParams); }
	
	public DBInfo(final String jdbcDriverName, 
			  	  final String jdbcUrl, 
			  	  final String jdbcUserName, 
			  	  final String jdbcPassWord, 
			  	  final String sql)
	{ init(jdbcDriverName, jdbcUrl, jdbcUserName, jdbcPassWord, sql, Collections.emptyList()); }
	
	public DBInfo(final String jdbcDriverName, 
			  final String jdbcUrl, 
			  final String jdbcUserName, 
			  final String jdbcPassWord)
	{ init(jdbcDriverName, jdbcUrl, jdbcUserName, jdbcPassWord, "", Collections.emptyList()); }
}