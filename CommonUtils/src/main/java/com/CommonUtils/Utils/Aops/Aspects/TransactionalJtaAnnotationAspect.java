package com.CommonUtils.Utils.Aops.Aspects;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.CommonUtils.Utils.Aops.Annotations.TransactionalJtaAnnotation;
import com.CommonUtils.Utils.DynaticUtils.Services.Impls.BeanUtilServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class TransactionalJtaAnnotationAspect 
{
	@Around(value = "@within(transactionalJtaAnnotation) || " + "@annotation(transactionalJtaAnnotation)")
	public Optional<Object> doAroundTransactionalJtaAnnotation(ProceedingJoinPoint pjp, TransactionalJtaAnnotation transactionalJtaAnnotation)
	{
		String transactionManagerName = transactionalJtaAnnotation.transactionManager();
		Object result = null;
		try
		{
			JtaTransactionManager jtaTransactionManager = BeanUtilServiceImpl.getBean(transactionManagerName, JtaTransactionManager.class)
																			 .orElseThrow(() -> { return new Exception("找不到指定的分布式事务管理器！！需查找的分布式事务管理器名字为：" + transactionManagerName); });
			jtaTransactionManager.getUserTransaction().begin();
			result = pjp.proceed();
			jtaTransactionManager.getUserTransaction().commit();
		}
		catch (Throwable ex)
		{ log.error("基于JTA进行分布式事务处理出现异常，事务管理器为{}，异常原因为：", transactionManagerName, ex); }
		
		return Optional.ofNullable(result);
	}
}