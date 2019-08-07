package com.CommonUtils.Jdbc.Bean.DBTable;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.map.MultiKeyMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public class Table implements Serializable
{
	private static final long serialVersionUID = 5591599328261381982L;
	
	private String tableName;
	
	/**这里使用了MultiKeyMap包装Column类，第一顺序key为columnLabel，第二顺序key为columnName*/
	private List<MultiKeyMap<String, Column>> columns;
	private String insertSqlForColumnName;
	private String insertSqlForColumnLabel;
}