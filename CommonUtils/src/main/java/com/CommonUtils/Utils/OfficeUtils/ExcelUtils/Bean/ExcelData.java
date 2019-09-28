package com.CommonUtils.Utils.OfficeUtils.ExcelUtils.Bean;

import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Deprecated
@Accessors(chain = true)
@Getter
@Setter
@ToString
public final class ExcelData 
{
	//Excel文档sheet名称
	private String sheetName;
	
	//sheet里面的每行数据
	private Collection<Map<String, Object>> rows;
}