package com.CommonUtils.Utils.TreeUtils.Bean;

import java.util.Collection;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class TreeNode<T>
{
	private T data;
	private Collection<TreeNode<T>> children;
	
	public TreeNode(final T data)
	{ this.data = data; }
	
	public boolean haveChildren()
	{ return !CollUtil.isEmpty(this.children); }
}