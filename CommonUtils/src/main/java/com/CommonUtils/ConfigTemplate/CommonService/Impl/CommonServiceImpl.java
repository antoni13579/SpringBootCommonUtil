package com.CommonUtils.ConfigTemplate.CommonService.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.Consumer;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.CommonUtils.ConfigTemplate.CommonService.ICommonService;
import com.CommonUtils.Utils.Annotations.TransactionalJta;
import com.CommonUtils.Utils.DBUtils.RedisUtil;
import com.CommonUtils.Utils.DBUtils.Bean.RedisEntry;
import com.CommonUtils.Utils.SystemUtils.ElasticSearchUtils.TransportClientUtil;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Service("commonService")
@Slf4j
public class CommonServiceImpl implements ICommonService
{
	@Resource
	private JdbcTemplate mydb1JdbcTemplate;
	
	@Resource
	private TransactionTemplate mydb1TransactionTemplate;
	
	@Resource
	private DatabaseClient mydb2DatabaseClient;
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource
	private KafkaTemplate<String, byte[]> kafkaTemplate;
	
	@Resource
	private TransportClient transportClient;
	
	@Override
	public void r2dbc()
	{
		Iterable<Map<String, Object>> iterable = this.mydb2DatabaseClient.execute("select 1 + 1 as result").fetch().all().toIterable();
		log.info("测试r2dbc的结果为：{}", IterUtil.toList(iterable).toString());
	}
	
	@Override
	public void elasticSearchByTransportClient()
	{
		boolean createIndexResult = TransportClientUtil.createIndex(this.transportClient, "my_elasticsearch_db1");
		if (createIndexResult)
		{ log.info("ElasticSearch测试（基于TransportClient）-----创建索引--------------已完成"); }
		else
		{ log.warn("ElasticSearch测试（基于TransportClient）-----创建索引--------------索引已存在，无需创建"); }
		
		
		boolean deleteIndexResult = TransportClientUtil.deleteIndex(this.transportClient, "my_elasticsearch_db1");
		if (deleteIndexResult)
		{ log.info("ElasticSearch测试（基于TransportClient）-----删除索引--------------已完成"); }
		else
		{ log.warn("ElasticSearch测试（基于TransportClient）------删除索引--------------索引已删除，无需重复处理"); }
	}
	
	@TransactionalJta(transactionManager = "txManager")
	@Override
	public void jtaTransactionForDB()
	{ log.info("测试分布式事务"); }
	
	/**测试成功*/
	/**
	 * Redis也能使用事务，但是需要有JDBC的事务管理器支持，
	 * 至于kafka是不能共用，因为kafka有自己的事务管理器，与DB的事务管理器不相同
	 * */
	@Transactional(transactionManager="mydb1DataSourceTransactionManager", rollbackFor = Exception.class)
	@Override
	public void localTransactionByAnnotation() throws CommonServiceException
	{
		log.info("开始测试注解形式的DB、REDIS事务");
		this.mydb1JdbcTemplate.update("insert into table1(id) values(?)", 123);
		RedisUtil.setForValue(this.redisTemplate, new RedisEntry<String, Object>("123", 456));
	}
	
	/**测试成功*/
	@Override
	public void localTransactionForDB()
	{
		this.mydb1TransactionTemplate.executeWithoutResult
		(
				transactionStatus -> 
				{
					try
					{
						log.info("开始测试编程形式的DB事务");
						this.mydb1JdbcTemplate.update("insert into table1(id) values(?)", 1);
					}
					catch (Exception ex)
					{
						transactionStatus.setRollbackOnly();
						log.error("测试编程形式的数据库事务，出现了异常，异常原因为：", ex);
					}
				}
		);
	}
	
	/**测试成功*/
	@Override
	public void localTransactionForRedis()
	{
		this.redisTemplate.executePipelined
		(
				new SessionCallback<List<Object>>()
				{
					@Override
					public <K, V> List<Object> execute(RedisOperations<K, V> operations)
					{
						List<Object> result = null;
						try
						{
							log.info("开始测试编程形式的Redis事务");
							redisTemplate.multi();
							RedisUtil.setForValue(redisTemplate, new RedisEntry<String, Object>("key1", "value1"));
							redisTemplate.convertAndSend("channel1", "我是需要Redis监听的消息");
						    result = redisTemplate.exec();
						    log.info("测试编程形式的Redis事务，存放在Redis的值为{}", RedisUtil.getForValue(redisTemplate, "key1"));
						}
						catch (Exception ex)
						{
							redisTemplate.discard();
							result = Collections.emptyList();
							log.error("测试编程形式的Redis事务，出现了异常，异常原因为：", ex);
						}
						return result;
					}
				}
		);
	}
	
	/**测试成功*/
	@Override
	public void localTransactionForKafka()
	{
		this.kafkaTemplate.executeInTransaction
		(
				operations -> 
				{
					log.info("开始测试编程形式的kafka事务");
					List<String> datas = new ArrayList<>();
					datas.add("4");
					datas.add("5");
					return operations.send("myTopic", ObjectUtil.serialize(datas));
				}
		);
	}
	
	/**测试成功*/
	@Transactional(transactionManager="kafkaTransactionManager", rollbackFor = Exception.class)
	@Override
	public void localTransactionByAnnotationWithKafka() throws CommonServiceException
	{
		log.info("开始测试注解形式的kafka事务");
		List<String> datas = new ArrayList<>();
		datas.add("1");
		datas.add("2");
		datas.add("3");
		this.kafkaTemplate.send("myTopic", ObjectUtil.serialize(datas));
	}
	
	@KafkaListener(containerFactory="kafkaListenerContainerFactory", topics= {"myTopic"}, groupId="myTopicGroup1")
	public void ackListener(byte[] record, Acknowledgment ack, Consumer<String, byte[]> consumer) 
	{
		List<String> value = ObjectUtil.deserialize(record);
		log.info("这个是消费者接收到的信息，信息为{}", value);
		ack.acknowledge();
	}
	
	public static class CommonServiceException extends Exception
	{
		private static final long serialVersionUID = -2788194271789969197L;

		public CommonServiceException(final String message)
		{ super(message); }
	}
}