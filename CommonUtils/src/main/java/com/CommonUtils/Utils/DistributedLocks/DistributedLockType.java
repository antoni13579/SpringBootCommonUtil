package com.CommonUtils.Utils.DistributedLocks;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum DistributedLockType 
{
	REDIS("基于Redis的分布式锁"),
	ZOOKEEPER("基于Zookeeper的分布式锁");
	
	private final String distributedLockType;
	
	private DistributedLockType(final String distributedLockType) 
	{ this.distributedLockType = distributedLockType; }
}