package com.CommonUtils.Utils.DBUtils;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum PreparedStatementOperationType 
{
	READ("以读取模式配置PreparedStatement"),
	WRITE("以修改模式配置PreparedStatement");
	
	private final String preparedStatementOperationType;
	
	private PreparedStatementOperationType(final String preparedStatementOperationType) 
	{ this.preparedStatementOperationType = preparedStatementOperationType; }
}