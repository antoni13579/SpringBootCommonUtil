package com.CommonUtils.Utils.Annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;

import org.springframework.stereotype.Component;

import com.CommonUtils.Utils.DistributedLocks.DistributedLockType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DistributedLockAnnotation
{
	String moduleName();
	DistributedLockType distributedLockType();
}