package com.CommonUtils.Kafka.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public final class KafkaMessage<K, V>
{
	private String topic;
	private K key;
	private V data;
}