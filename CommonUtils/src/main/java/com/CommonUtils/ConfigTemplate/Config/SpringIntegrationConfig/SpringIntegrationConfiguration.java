package com.CommonUtils.ConfigTemplate.Config.SpringIntegrationConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.web.context.request.async.DeferredResult;

import com.CommonUtils.ConfigTemplate.Bean.ExcelBean;
import com.CommonUtils.Utils.OfficeUtils.ExcelUtils.Bean.ExcelData;
import com.CommonUtils.Utils.TreeUtils.TreeUtil;
import com.CommonUtils.Utils.TreeUtils.Bean.TreeNode;

import cn.hutool.core.collection.CollUtil;

/**
* 配置IntegrationFlow要注意，input与Output参数都不能为空，同时，如果执行过程中直接中断，会导致Spring Integration提示Dispatcher has no subscribers异常，主要是Spring关闭的时候没有断离好这些流程，导致异常，是框架本身的问题
*
* */
@Configuration
public class SpringIntegrationConfiguration 
{
	private Collection<TreeNode<Map<String, Object>>> orginalTreeNodes = null;
	
	@Bean(name = "startToHandleExcelData")
	public IntegrationFlow startToHandleExcelData()
	{
		return flow -> flow
				.<ExcelBean, Boolean>route
				(
						ExcelBean::isEmpty,
						
						mapping -> mapping
						.subFlowMapping
						(
								true, 
								subFlow -> subFlow
								.<ExcelBean, DeferredResult<Collection<TreeNode<Map<String, Object>>>>>transform
								(
										excelBean -> 
										{
											this.orginalTreeNodes = Collections.emptyList();
											Collection<TreeNode<Map<String, Object>>> coll = TreeUtil.getTree(orginalTreeNodes, "CELL5", "CELL1");
											excelBean.getDeferredResult().setResult(coll);
											return excelBean.getDeferredResult();
										}
								)
								.handle(msg -> {})
						)
						
						.subFlowMapping
						(
								false, 
								subFlow -> subFlow
								.<ExcelBean, DeferredResult<Collection<TreeNode<Map<String, Object>>>>>transform
								(
										excelBean -> 
										{
											ExcelData excelData = CollUtil.get(excelBean.getExcelDatas(), 0);
											this.orginalTreeNodes = TreeUtil.excelDataToTreeNodes(excelData);
											Collection<TreeNode<Map<String, Object>>> coll = TreeUtil.getTree(orginalTreeNodes, "CELL5", "CELL1");
											excelBean.getDeferredResult().setResult(coll);
											return excelBean.getDeferredResult();
										}
								)
								.handle(msg -> {})
						)
				)
				;
	}
}