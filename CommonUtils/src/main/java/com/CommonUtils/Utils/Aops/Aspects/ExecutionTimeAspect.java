package com.CommonUtils.Utils.Aops.Aspects;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.CommonUtils.Utils.Annotations.ExecutionTime;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**建议使用cn.hutool.core.date.StopWatch
 * @deprecated
 * */
@Deprecated(since="建议使用cn.hutool.core.date.StopWatch")
@Aspect
@Component
@Slf4j
public final class ExecutionTimeAspect 
{
	@Around(value = "@within(executionTime) || " + "@annotation(executionTime)")
	public Optional<Object> doAroundExecutionTime(ProceedingJoinPoint pjp, ExecutionTime executionTime)
	{
		String moduleName = executionTime.moduleName();
		Object result = null;
		if (StringUtil.isStrEmpty(moduleName))
		{
			log.warn("没有指定对应的模块名称，ExecutionTimeAnnotation程序直接结束");
			return Optional.ofNullable(result);
		}
		
		
		try
		{
			log.info("=========================================【{}】开始执行=================================================", moduleName);
			long startTime = System.currentTimeMillis();
			result = pjp.proceed();
			long endTime = System.currentTimeMillis() - startTime;
			double seconds = endTime / 1000D;
			log.info("=========================================【{}】执行结束，耗时：{}秒=========================================", moduleName, seconds);
		}
		catch (Throwable ex) { log.error("【{}】Aop（ExecutionTimeAnnotationAspect）执行失败，异常原因为：{}", moduleName, ex); }
		
		return Optional.ofNullable(result);
	}
}