package com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public abstract class AbstractDBInfo  
{
	protected String sql;
	protected List<Object[]> bindingParams;
	protected boolean useColumnName;
}