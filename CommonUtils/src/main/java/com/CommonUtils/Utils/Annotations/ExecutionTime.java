package com.CommonUtils.Utils.Annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**建议使用cn.hutool.core.date.StopWatch
 * @deprecated
 * */
@Deprecated(since="建议使用cn.hutool.core.date.StopWatch")
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ExecutionTime
{ String moduleName(); }