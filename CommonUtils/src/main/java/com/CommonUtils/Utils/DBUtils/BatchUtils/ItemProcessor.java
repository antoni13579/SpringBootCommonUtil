package com.CommonUtils.Utils.DBUtils.BatchUtils;

@FunctionalInterface
public interface ItemProcessor<T>
{ T process(final T itemData); }