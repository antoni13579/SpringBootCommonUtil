package com.CommonUtils.ConfigTemplate.CommonService.Impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.CommonUtils.ConfigTemplate.CommonService.ICommonService;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.entity.TestData;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.service.ITestDataService;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.entity.Demo;
import com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.service.IDemoService;
import com.CommonUtils.Utils.DBUtils.RedisUtil;
import com.CommonUtils.Utils.DBUtils.Bean.RedisEntry;

import lombok.extern.slf4j.Slf4j;

//@Service("commonService")
@Slf4j
public class CommonServiceImpl implements ICommonService
{
	@Resource
	private IDemoService demoService;
	
	@Resource
	private ITestDataService testDataService;
	
	@Resource
	private JdbcTemplate myJdbcTemplate;
	
	@Resource
	private TransactionTemplate myTransactionTemplate;
	
	@Resource
	private JtaTransactionManager jtaTransactionManager;
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Override
	public void jtaTransactionForDB()
	{
		try
		{
			this.jtaTransactionManager.getUserTransaction().begin();
			
			this.demoService.saveOrUpdate(new Demo().setId("1").setAge(123));
			this.testDataService.saveOrUpdate(new TestData().setId("1").setStatus("1").setCreateBy("1").setCreateDate(new Date()).setUpdateBy("1").setUpdateDate(new Date()));
			
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
	
	/**
	 * Redis也能使用事务，但是需要有JDBC的事务管理器支持，
	 * 至于kafka是不能共用，因为kafka有自己的事务管理器，与DB的事务管理器不相同
	 * */
	@Transactional(transactionManager="myDataSourceTransactionManager", rollbackFor = Exception.class)
	@Override
	public void localTransactionByAnnotation() throws Exception
	{
		this.myJdbcTemplate.update("insert into test_data(id, status, create_by, create_date, update_by, update_date) values(?, ?, ?, ?, ?, ?)", "1", "1", "1", new Date(), "1", new Date());
		RedisUtil.setForValue(this.redisTemplate, new RedisEntry<String, Object>("123", 456));
	}
	
	@Override
	public void localTransactionForDB()
	{
		this.myTransactionTemplate.executeWithoutResult
		(
				transactionStatus -> 
				{
					try
					{ this.myJdbcTemplate.update("insert into test_data(id, status, create_by, create_date, update_by, update_date) values(?, ?, ?, ?, ?, ?)", "1", "1", "1", new Date(), "1", new Date()); }
					catch (Exception ex)
					{ transactionStatus.setRollbackOnly(); }
				}
		);
	}
	
	@Override
	public void localTransactionForRedis()
	{
		this.redisTemplate.execute
		(
				new SessionCallback<List<Object>>()
				{
					@Override
					public List<Object> execute(RedisOperations operations) throws DataAccessException 
					{
						List<Object> result = null;
						try
						{
							operations.multi();
						    operations.opsForSet().add("key", "value1");
						    result = operations.exec();
						}
						catch (Exception ex)
						{
							operations.discard();
							result = Collections.emptyList();
						}
						return result;
					}
				}
		);
	}
	
	@Override
	public void localTransactionForKafka()
	{ this.kafkaTemplate.executeInTransaction(operations -> { return operations.send("topic", "data"); }); }
	
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