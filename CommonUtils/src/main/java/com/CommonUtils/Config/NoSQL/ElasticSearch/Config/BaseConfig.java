package com.CommonUtils.Config.NoSQL.ElasticSearch.Config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public final class BaseConfig 

/*
# Elasticsearch
# 9200端口是用来让HTTP REST API来访问ElasticSearch，而9300端口是传输层监听的默认端口
elasticsearch.ip=192.168.30.128
elasticsearch.port=9300
elasticsearch.pool=5
#注意cluster.name需要与config/elasticsearch.yml中的cluster.name一致
elasticsearch.cluster.name=my-application

elasticsearch.cluster-name=my-application
elasticsearch.name=my-elasticsearch-node-1
elasticsearch.host=127.0.0.1
elasticsearch.port=9300
*/
	
{
	private BaseConfig() {}
	
	public static TransportClient getTransportClient(final String clusterName, 
													 //final String name, 
													 //final String host,
													 //final int port,
													 final Map<String, Integer> hostAndPorts,
													 final int poolSize) throws UnknownHostException
	{
		TransportClient client = new PreBuiltTransportClient
		(
				Settings.builder()
						.put("cluster.name", clusterName)
						//.put("name", name)
						//.put("cluster.nodes", host + ":" + port)
						.put("client.transport.sniff", true)//增加嗅探机制，找到ES集群
						.put("thread_pool.search.size", poolSize)//增加线程池个数，暂时设为5
						.build()
		);
		
		Iterator<Entry<String, Integer>> entries = hostAndPorts.entrySet().iterator();
		while (entries.hasNext())
		{
			Entry<String, Integer> entry = entries.next();
			String host = entry.getKey();
			int port = entry.getValue();
			
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
		}

		return client;
	}
}