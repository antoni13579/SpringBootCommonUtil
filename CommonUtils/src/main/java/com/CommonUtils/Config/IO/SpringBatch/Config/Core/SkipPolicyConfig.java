package com.CommonUtils.Config.IO.SpringBatch.Config.Core;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.NeverSkipItemSkipPolicy;

public final class SkipPolicyConfig 
{
	private SkipPolicyConfig() {}
	
	private static NeverSkipItemSkipPolicy NEVER_SKIP_ITEM_SKIP_POLICY_INSTANCE = null;
	
	private static AlwaysSkipItemSkipPolicy ALWAYS_SKIP_ITEM_SKIP_POLICY_INSTANCE = null;
	
	/**
	 * SpringBatch Skip规则器
	 * 规则：遇到错误总是跳过
	 * */
	public static AlwaysSkipItemSkipPolicy getAlwaysSkipItemSkipPolicyInstance()
	{
		if (null == ALWAYS_SKIP_ITEM_SKIP_POLICY_INSTANCE)
		{
			synchronized (SkipPolicyConfig.class)
			{
				if (null == ALWAYS_SKIP_ITEM_SKIP_POLICY_INSTANCE)
				{ ALWAYS_SKIP_ITEM_SKIP_POLICY_INSTANCE = new AlwaysSkipItemSkipPolicy(); }
			}
		}
		
		return ALWAYS_SKIP_ITEM_SKIP_POLICY_INSTANCE;
	}
	
	/**
	 * SpringBatch Skip规则器
	 * 规则：遇到错误不能跳过
	 * */
	public static NeverSkipItemSkipPolicy getNeverSkipItemSkipPolicyInstance()
	{
		if (null == NEVER_SKIP_ITEM_SKIP_POLICY_INSTANCE)
		{
			synchronized (SkipPolicyConfig.class)
			{
				if (null == NEVER_SKIP_ITEM_SKIP_POLICY_INSTANCE)
				{ NEVER_SKIP_ITEM_SKIP_POLICY_INSTANCE = new NeverSkipItemSkipPolicy(); }
			}
		}
		
		return NEVER_SKIP_ITEM_SKIP_POLICY_INSTANCE;
	}
}