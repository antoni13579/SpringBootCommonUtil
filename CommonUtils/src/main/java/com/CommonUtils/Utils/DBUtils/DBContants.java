package com.CommonUtils.Utils.DBUtils;

import lombok.Getter;
import lombok.ToString;

public final class DBContants 
{
	private DBContants() {}
	
	public static final int inSqlStatementForOracle = 1000;
	
	public static final int inSqlStatementForMySql = 100;
	
	public static final int fetchSize = 10000;
	
	public static final String TERADATA_JDBC_DRIVER = "com.teradata.jdbc.TeraDriver";
	
	@ToString
	@Getter
	public enum OracleJdbcDriver
	{
		ORACLE_NEW("oracle.jdbc.OracleDriver"),
		ORACLE_OLD("oracle.jdbc.driver.OracleDriver"),
		ORACLE_XA_NEW("oracle.jdbc.xa.client.OracleXADataSource"),
		ORACLE_XA_OLD("oracle.jdbc.xa.OracleXADataSource");
		
		private final String jdbcDriver;
		
		private OracleJdbcDriver(final String jdbcDriver)
		{ this.jdbcDriver = jdbcDriver; }
	}
	
	@ToString
	@Getter
	public enum MySqlJdbcDriver
	{
		MYSQL_NEW("com.mysql.cj.jdbc.Driver"),
		MYSQL_OLD("com.mysql.jdbc.Driver"),
		MYSQL_XA("com.mysql.cj.jdbc.MysqlXADataSource");
		
		private final String jdbcDriver;
		
		private MySqlJdbcDriver(final String jdbcDriver)
		{ this.jdbcDriver = jdbcDriver; }
	}
	
	@ToString
	@Getter
	public enum PostgresqlJdbcDriver
	{
		POSTGRESQL("org.postgresql.Driver"),
		POSTGRESQL_XA("org.postgresql.xa.PGXADataSource");
		
		private final String jdbcDriver;
		
		private PostgresqlJdbcDriver(final String jdbcDriver)
		{ this.jdbcDriver = jdbcDriver; }
	}
	
	@ToString
	@Getter
	public enum SqlServerJdbcDriver
	{
		MICROSOFT("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
		MICROSOFT_XA("com.microsoft.sqlserver.jdbc.SQLServerXADataSource"),
		NET_SOURCEFORGE("net.sourceforge.jtds.jdbc.Driver");
		
		private final String jdbcDriver;
		
		private SqlServerJdbcDriver(final String jdbcDriver)
		{ this.jdbcDriver = jdbcDriver; }
	}
}