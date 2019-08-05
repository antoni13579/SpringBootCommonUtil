package com.CommonUtils.Utils.DBUtils;

import lombok.Getter;
import lombok.ToString;

public final class DBContants 
{
	private DBContants() {}
	
	public static final int inSqlStatementForOracle = 1000;
	
	public static final int inSqlStatementForMySql = 100;
	
	public static final int fetchSize = 10000;
	
	public static final String ORACLE_JDBC_DRIVER_NEW = "oracle.jdbc.OracleDriver";
	public static final String ORACLE_JDBC_DRIVER_OLD = "oracle.jdbc.driver.OracleDriver";
	
	public static final String TERADATA_JDBC_DRIVER = "com.teradata.jdbc.TeraDriver";
	
	public static final String MYSQL_JDBC_DRIVER_NEW = "com.mysql.cj.jdbc.Driver";
	public static final String MYSQL_JDBC_DRIVER_OLD = "com.mysql.jdbc.Driver";
	
	public static final String POSTGRESQL_JDBC_DRIVER = "org.postgresql.Driver";
	
	@ToString
	@Getter
	public enum SqlServerJdbcDriver
	{
		MICROSOFT("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
		NET_SOURCEFORGE("net.sourceforge.jtds.jdbc.Driver");
		
		private final String jdbcDriver;
		
		private SqlServerJdbcDriver(final String jdbcDriver)
		{ this.jdbcDriver = jdbcDriver; }
	}
}