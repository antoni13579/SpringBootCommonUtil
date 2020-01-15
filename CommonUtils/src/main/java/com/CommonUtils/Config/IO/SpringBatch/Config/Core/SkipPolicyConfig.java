package com.CommonUtils.Config.IO.SpringBatch.Config.Core;

import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.NeverSkipItemSkipPolicy;

public final class SkipPolicyConfig 
{
	private SkipPolicyConfig() {}
	
	/**
	 * SpringBatch Skip规则器
	 * 规则：遇到错误总是跳过
	 * */
	public static AlwaysSkipItemSkipPolicy getAlwaysSkipItemSkipPolicyInstance()
	{ return AlwaysSkipItemSkipPolicySingletonContainer.instance; }
	
	/**
	 * SpringBatch Skip规则器
	 * 规则：遇到错误不能跳过
	 * */
	public static NeverSkipItemSkipPolicy getNeverSkipItemSkipPolicyInstance()
	{ return NeverSkipItemSkipPolicySingletonContainer.instance; }
	
	private static class NeverSkipItemSkipPolicySingletonContainer
	{ private static NeverSkipItemSkipPolicy instance = new NeverSkipItemSkipPolicy(); }
	
	private static class AlwaysSkipItemSkipPolicySingletonContainer
	{ private static AlwaysSkipItemSkipPolicy instance = new AlwaysSkipItemSkipPolicy(); }
}