package com.CommonUtils.Utils.ThreadUtils;

import java.util.Map;

@FunctionalInterface
public interface StampedLockProcess 
{ Map<String, Object> process(final Map<String, Object> params); }