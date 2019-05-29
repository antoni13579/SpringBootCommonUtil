package com.CommonUtils.Utils.DBUtils;

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
	
	public static final String SQL_SERVER_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
}