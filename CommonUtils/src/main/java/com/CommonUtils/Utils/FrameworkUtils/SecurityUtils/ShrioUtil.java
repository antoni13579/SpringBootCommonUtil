package com.CommonUtils.Utils.FrameworkUtils.SecurityUtils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public final class ShrioUtil 
{
	private ShrioUtil() {}
	
	public static Subject getSubject()
	{ return SecurityUtils.getSubject(); }
}