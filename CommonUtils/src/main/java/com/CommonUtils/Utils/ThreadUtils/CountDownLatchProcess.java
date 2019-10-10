package com.CommonUtils.Utils.ThreadUtils;

import java.util.concurrent.CountDownLatch;

@FunctionalInterface
public interface CountDownLatchProcess<T>
{ T process(final CountDownLatch countDownLatch, final T params); }