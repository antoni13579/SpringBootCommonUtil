package com.CommonUtils.Jdbc.Bean.DBTable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public class Column implements Serializable
{
	private static final long serialVersionUID = -5658243225486350831L;
	
	private int indx;
	private String columnName;
	private String columnLabel;
	
	/**对应于java.sql.Types*/
	private int columnTypeForJdbc;
	private String columnTypeNameForJdbc;
	
	private String columnTypeNameForJava;
	
	private Object columnValue;
}