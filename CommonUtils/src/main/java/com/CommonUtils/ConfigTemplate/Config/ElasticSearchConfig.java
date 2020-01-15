package com.CommonUtils.ConfigTemplate.Config;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = { "com.CommonUtils.ConfigTemplate.ElasticSearch.Dao" })
public class ElasticSearchConfig 
{
	@Bean(destroyMethod="close")
	public TransportClient transportClient() throws UnknownHostException
	{
		Map<String, Integer> hostAndPorts = new HashMap<>();
		hostAndPorts.put("127.0.0.1", 9300);
		return com.CommonUtils.Config.NoSQL.ElasticSearch.Config.BaseConfig.getTransportClient("my-application", hostAndPorts, 5);
	}
	
	@Bean
	public ElasticsearchTemplate elasticSearchTemplate(@Qualifier("transportClient")TransportClient transportClient)
	{ return new ElasticsearchTemplate(transportClient); }
}