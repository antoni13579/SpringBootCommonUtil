package com.CommonUtils.Config.SQL.Jdbc.Config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import cn.hutool.core.util.ArrayUtil;

public final class TxAdviceAdvisorConfig 
{
	private TxAdviceAdvisorConfig() {}
	
	public static Advisor getInstance(final String aopPointcutExpression, final DataSourceTransactionManager dataSourceTransactionManager)
	{
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(aopPointcutExpression);
		return new DefaultPointcutAdvisor(pointcut,
										  new TransactionInterceptor(dataSourceTransactionManager, TransactionAttributeConfig.getInstance()));
	}
	
	public static String getAopPointcutExpression(final String ... paths)
	{
		StringBuilder sb = new StringBuilder();
		if (!ArrayUtil.isEmpty(paths))
		{
			for (int i = 0; i < paths.length; i++)
			{
				sb.append("(execution(* ");
				sb.append(paths[i]);
				sb.append("..*.*(..)))");
				
				if (i < paths.length - 1)
				{ sb.append(" or "); }
			}
		}
		
		return sb.toString();
	}
}