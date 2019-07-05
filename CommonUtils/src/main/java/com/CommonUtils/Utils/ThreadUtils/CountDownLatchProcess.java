package com.CommonUtils.Utils.ThreadUtils;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@FunctionalInterface
public interface CountDownLatchProcess 
{ Map<String, Object> process(final CountDownLatch countDownLatch, final Map<String, Object> params); }