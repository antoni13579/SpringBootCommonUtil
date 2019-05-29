package com.CommonUtils.ConfigTemplate.Bean;

import java.util.Collection;
import java.util.Map;

import org.springframework.web.context.request.async.DeferredResult;

import com.CommonUtils.Utils.Office.Excel.Bean.ExcelData;
import com.CommonUtils.Utils.TreeUtils.Bean.TreeNode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public class ExcelBean
{
	private Collection<ExcelData> excelDatas;
	private boolean empty;
	private DeferredResult<Collection<TreeNode<Map<String, Object>>>> deferredResult;
}