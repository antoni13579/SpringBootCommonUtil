package com.CommonUtils.Redis.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public final class RedisEntry<K, V>
{
	private K key;
	private V value;
}