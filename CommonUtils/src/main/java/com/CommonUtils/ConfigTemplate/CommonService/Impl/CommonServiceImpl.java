package com.CommonUtils.ConfigTemplate.CommonService.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.Consumer;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.dao.DataAccessException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.concurrent.ListenableFuture;

import com.CommonUtils.ConfigTemplate.CommonService.ICommonService;
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
	private JtaTransactionManager jtaTransactionManager;
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource
	private KafkaTemplate<String, byte[]> kafkaTemplate;
	
	@Resource
	private TransportClient transportClient;
	
	@Resource
	private DatabaseClient mydb2DatabaseClient;
	
	@Override
	public void r2dbc()
	{
		Iterable<Map<String, Object>> iterable = this.mydb2DatabaseClient.execute("select 1 + 1").fetch().all().toIterable();
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
	
	@Override
	public void jtaTransactionForDB()
	{
		try
		{
			this.jtaTransactionManager.getUserTransaction().begin();
			this.jtaTransactionManager.getUserTransaction().commit();
		}
		catch (Exception ex)
		{
			log.error("基于JTA进行分布式事务处理出现异常，异常原因为：", ex);
			if (null != this.jtaTransactionManager)
			{
				try 
				{ this.jtaTransactionManager.getUserTransaction().rollback(); }
				catch (Exception e)
				{ log.error("基于JTA进行分布式事务处理，出现异常了，在回滚的时候出现异常了，异常原因为：", e); }
			}
		}
	}
	
	/**测试成功*/
	/**
	 * Redis也能使用事务，但是需要有JDBC的事务管理器支持，
	 * 至于kafka是不能共用，因为kafka有自己的事务管理器，与DB的事务管理器不相同
	 * */
	@Transactional(transactionManager="mydb1DataSourceTransactionManager", rollbackFor = Exception.class)
	@Override
	public void localTransactionByAnnotation() throws Exception
	{
		log.info("开始测试注解形式的DB、REDIS事务");
		this.mydb1JdbcTemplate.update("insert into table1(id) values(?)", 123);
		RedisUtil.setForValue(this.redisTemplate, new RedisEntry<String, Object>("123", 456));
		//int i = 1/0;
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
						//int i = 1/0;
					}
					catch (Exception ex)
					{
						transactionStatus.setRollbackOnly();
						ex.printStackTrace();
					}
				}
		);
	}
	
	/**测试成功*/
	@Override
	public void localTransactionForRedis()
	{
		this.redisTemplate.execute
		(
				new SessionCallback<Object>()
				{
					@Override
					public Object execute(RedisOperations operations) throws DataAccessException 
					{
						Object result = null;
						try
						{
							log.info("开始测试编程形式的Redis事务");
							operations.multi();
							RedisUtil.setForValue(operations, new RedisEntry<String, String>("key1", "value1"));
						    //int i = 1/0;
							operations.convertAndSend("channel1", "我是需要Redis监听的消息");
						    result = operations.exec();
						    log.info("测试编程形式的Redis事务，存放在Redis的值为{}", RedisUtil.getForValue(operations, "key1"));
						}
						catch (Exception ex)
						{
							operations.discard();
							result = Collections.emptyList();
							ex.printStackTrace();
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
					datas.add("6");
					ListenableFuture<SendResult<String, byte[]>> result = operations.send("myTopic", ObjectUtil.serialize(datas));
					//int i = 1/0;
					return result;
				}
		);
	}
	
	/**测试成功*/
	@Transactional(transactionManager="kafkaTransactionManager", rollbackFor = Exception.class)
	@Override
	public void localTransactionByAnnotationWithKafka() throws Exception
	{
		log.info("开始测试注解形式的kafka事务");
		List<String> datas = new ArrayList<>();
		datas.add("1");
		datas.add("2");
		datas.add("3");
		this.kafkaTemplate.send("myTopic", ObjectUtil.serialize(datas));
		//int i = 1/0;
	}
	
	@KafkaListener(containerFactory="kafkaListenerContainerFactory", topics= {"myTopic"}, groupId="myTopicGroup1")
	public void ackListener(byte[] record, Acknowledgment ack, Consumer<String, byte[]> consumer) 
	{
		List<String> value = ObjectUtil.deserialize(record);
		log.info("这个是消费者接收到的信息，信息为{}", value);
		ack.acknowledge();
	}
	
	/*适用于事务下唯一数据源切换
	 * @Transactional(rollbackFor={Exception.class,RuntimeException.class})
@DataSource("master")
    public GmcSmsInfo addMaster(GmcSmsInfo smsInfo) throws BusinessServiceException {
        try {
            smsInfo.setSmsId(gmcSmsInfoDaoImpl.save(smsInfo));
        }
        catch (FrameworkDAOException e) {
            throw new BusinessServiceException(e);
        }
        return smsInfo;
    }
	 * 
	 * */
}