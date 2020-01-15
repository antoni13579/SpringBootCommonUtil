package com.CommonUtils.Utils.Aops.Aspects;

import java.util.Optional;

import javax.annotation.Resource;
import javax.transaction.UserTransaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.CommonUtils.Utils.Annotations.TransactionalJta;
import com.CommonUtils.Utils.DynaticUtils.Services.Impls.BeanUtilServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class TransactionalJtaAspect 
{
	@Resource
	private BeanUtilServiceImpl beanUtilServiceImpl;
	
	@Around(value = "@within(transactionalJta) || " + "@annotation(transactionalJta)")
	public Optional<Object> doAroundTransactionalJta(ProceedingJoinPoint pjp, TransactionalJta transactionalJta)
	{
		Optional<UserTransaction> userTransaction = Optional.ofNullable(null);
		Object result = null;
		try
		{
			UserTransaction tmp = this.beanUtilServiceImpl.getBean(transactionalJta.transactionManager(), JtaTransactionManager.class)
														  .orElseThrow()
														  .getUserTransaction();
			userTransaction = Optional.ofNullable(tmp);
			userTransaction.orElseThrow().begin();
			result = pjp.proceed();
			userTransaction.orElseThrow().commit();
		}
		catch (Throwable ex)
		{
			log.error("基于JTA进行分布式事务处理出现异常，异常原因为：", ex);
			try 
			{ userTransaction.orElseThrow().rollback(); }
			catch (Exception e)
			{ log.error("基于JTA进行分布式事务处理，出现异常了，在回滚的时候出现异常了，异常原因为：", e); }
		}
		return Optional.ofNullable(result);
	}
}