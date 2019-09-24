package com.CommonUtils.Utils.TreeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.CommonUtils.Utils.OfficeUtils.ExcelUtils.Bean.ExcelData;
import com.CommonUtils.Utils.TreeUtils.Bean.TreeNode;
import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;

public final class TreeUtil 
{
	private TreeUtil() {}
	
	public static <K, V> String getTreeJson(final Collection<TreeNode<Map<K, V>>> tree)
	{ return JSON.toJSONString(tree); }
	
	public static <K, V> String getTreeJson(final Collection<TreeNode<Map<K, V>>> originNodeList, final K rootKey, final K leafKey)
	{ return JSON.toJSONString(getTree(originNodeList, rootKey, leafKey)); }
	
	public static <K, V> Collection<TreeNode<Map<K, V>>> mapsToTreeNodes(final Collection<Map<K, V>> records)
	{
		if (!CollUtil.isEmpty(records))
		{
			Collection<TreeNode<Map<K, V>>> result = new ArrayList<>();
			for (Map<K, V> record : records)
			{ result.add(new TreeNode<Map<K, V>>(record)); }
			return result;
		}
		else
		{ return Collections.emptyList(); }
	}
	
	public static Collection<TreeNode<Map<String, Object>>> excelDataToTreeNodes(final ExcelData excelData)
	{
		if (null != excelData)
		{
			Collection<Map<String, Object>> rows = excelData.getRows();
			return mapsToTreeNodes(rows);
		}
		else
		{ return Collections.emptyList(); }
	}
	
	/**
	 * 生成基于Map的树形结构
	 * */
	public static <K, V> Collection<TreeNode<Map<K, V>>> getTree(final Collection<TreeNode<Map<K, V>>> originNodeList, final K rootKey, final K leafKey)
	{
		Collection<TreeNode<Map<K, V>>> result = new ArrayList<TreeNode<Map<K, V>>>();
		for (TreeNode<Map<K, V>> node1 : originNodeList)
		{
			boolean mark = false;
			V rootId = node1.getData().get(rootKey);
			for (TreeNode<Map<K, V>> node2 : originNodeList)
			{
				V leafId = node2.getData().get(leafKey);
				if (null != rootId && Objects.equals(rootId, leafId))
				{
					mark = true;
					if (CollUtil.isEmpty(node2.getChildren()))
					{ node2.setChildren(new ArrayList<TreeNode<Map<K, V>>>()); }
					
					if (!node2.getChildren().contains(node1))
					{ node2.getChildren().add(node1); }
				}
			}
			
			if (!mark)
			{ result.add(node1); }
		}
		
		return result;
	}
	
	/**
	 * 递归处理树子节点结构，对每个节点进行处理（根节点不能用这个方法）
	 * */
	@SafeVarargs
	public static <K, V> void recursionHandleTree(final TreeNode<Map<K, V>> node, final int level, final ItemProcess<Map<K, V>> ... itemProcesses)
	{
		if (node.haveChildren())
		{
			Collection<TreeNode<Map<K, V>>> children = node.getChildren();
			for (TreeNode<Map<K, V>> child : children)
			{
				if (!ArrayUtil.isEmpty(itemProcesses))
				{
					for (ItemProcess<Map<K, V>> itemProcess : itemProcesses)
					{ itemProcess.process(child.getData(), level); }
				}
				
				if (child.haveChildren())
				{
					int tempLevel = level;
					tempLevel++;
					recursionHandleTree(child, tempLevel, itemProcesses);
				}
			}
		}
	}
	
	/**
	 * 处理根节点与子节点
	 * */
	@SafeVarargs
	public static <K, V> void handleTree(final TreeNode<Map<K, V>> root, 
										 final int level, 
										 final ItemProcess<Map<K, V>> ... itemProcesses)
	{
		if (!ArrayUtil.isEmpty(itemProcesses))
		{
			for (ItemProcess<Map<K, V>> itemProcess : itemProcesses)
			{ itemProcess.process(root.getData(), level); }
		}
		
		recursionHandleTree(root, level + 1, itemProcesses);
	}
	
	@FunctionalInterface
	public interface ItemProcess<T>
	{ void process(final T data, final int level); }
}