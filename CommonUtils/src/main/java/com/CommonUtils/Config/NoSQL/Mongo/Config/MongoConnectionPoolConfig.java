package com.CommonUtils.Config.NoSQL.Mongo.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/**
 * 已过时
 * @deprecated
 * */
@Deprecated(since="已过时")
public final class MongoConnectionPoolConfig 
{
	private MongoConnectionPoolConfig() {}
	
	/**
     * MongoClient的实例代表数据库连接池，是线程安全的，可以被多线程共享，客户端在多线程条件下仅维持一个实例即可
     * Mongo是非线程安全的，目前mongodb API中已经建议用MongoClient替代Mongo
     */
	public static com.mongodb.MongoClient getInstance(final Map<String, Integer> hostAndPorts, final MongoCredential credential)
	{
		List<ServerAddress> seeds = new ArrayList<>();
		hostAndPorts.entrySet().forEach
		(
				entry -> 
				{
					String host = entry.getKey();
					int port = entry.getValue();
					seeds.add(new ServerAddress(host, port));
				}
		);
		
		return new com.mongodb.MongoClient
		(
				seeds, 
				credential, 
				MongoClientOptions.builder()
								  //.addClusterListener(clusterListener)
								  //.addCommandListener(commandListener)
				                  //.addConnectionPoolListener(connectionPoolListener)
				                  //.addServerListener(serverListener)
				                  //.addServerMonitorListener(serverMonitorListener)
				                  //.applicationName(applicationName)
				                  //.autoEncryptionSettings(autoEncryptionSettings)
				                  //.codecRegistry(codecRegistry)
				                  //.compressorList(compressorList)
				
								  //与目标数据库能够建立的最大connection数量为40
								  .connectionsPerHost(40)
								  
								  //与数据库建立连接的timeout设置为1分钟
								  //连接超时时间，默认值是0，就是不超时
				                  .connectTimeout(1000*60*1)
				                  
				                  //.cursorFinalizerEnabled(cursorFinalizerEnabled)
				                  //.dbDecoderFactory(dbDecoderFactory)
				                  //.dbEncoderFactory(dbEncoderFactory)
				                  
				                  //集群心跳连接的超时时间，3秒
								  .heartbeatConnectTimeout(3*1000)
				                  
				                  //驱动用来确保集群中服务器状态的心跳频率，10秒
				                  .heartbeatFrequency(10*1000)
				                  
				                  //集群心跳连接的socket超时时间，10秒
				                  .heartbeatSocketTimeout(10*1000)
				                  
				                  //.localThreshold(localThreshold)
				                  
				                  //连接池连接最大空闲时间，10分钟
				                  .maxConnectionIdleTime(1000*60*10)
				                  
				                  //连接池连接的最大存活时间，30分钟
				                  .maxConnectionLifeTime(1000*60*30)
								  
								  /*
								   *  一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为4分钟
								   *  这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
								   *  故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
						           */
				                  .maxWaitTime(1000*60*4)
								  
				                  //客户端最小连接数
				                  .minConnectionsPerHost(10)
				                  
				                  //驱动重新检查服务器状态最少等待时间，3秒
				                  .minHeartbeatFrequency(1000*3)
				                  
				                  //.readConcern(readConcern)
				                  
				                  /*
				                   * MongoDB有5种ReadPreference模式：
				                   * primary    主节点，默认模式，读操作只在主节点，如果主节点不可用，报错或者抛出异常。
				                   * primaryPreferred   首选主节点，大多情况下读操作在主节点，如果主节点不可用，如故障转移，读操作在从节点。
				                   * secondary    从节点，读操作只在从节点， 如果从节点不可用，报错或者抛出异常。
				                   * secondaryPreferred    首选从节点，大多情况下读操作在从节点，特殊情况（如单主节点架构）读操作在主节点。
				                   * nearest    最邻近节点，读操作在最邻近的成员，可能是主节点或者从节点。
				                   * */
				                  .readPreference(ReadPreference.secondaryPreferred())
				                  
				                  //.requiredReplicaSetName(requiredReplicaSetName)
				                  
								  .retryReads(true)
								  
				                  //.retryWrites(retryWrites)
				                  
								  //服务器查询超时时间，它定义驱动在抛出异常之前等待服务器查询成功，默认30s,单位milliseconds
								  .serverSelectionTimeout(1000 * 30)
								  
				                  //.serverSelector(serverSelector)
								  
								  //socket超时时间，默认值是0，就是不超时
								  //与数据库建立连接的timeout设置为2分钟
				                  .socketTimeout(1000*60*2)
				                  
				                  //.sslContext(sslContext)
				                  
				                  //驱动是否使用ssl进行连接，默认是false
				                  .sslEnabled(false)
				                  
				                  .sslInvalidHostNameAllowed(false)
								  
								  //如果当前所有的connection都在使用中，则每个connection上可以有40个线程排队等待
								  //可被阻塞的线程数因子，默认值为5，如果connectionsPerHost配置为10，那么最多能阻塞50个线程，超过50个之后就会收到一个异常
				                  .threadsAllowedToBlockForConnectionMultiplier(40)
				                  
				                  /*
				                   * WriteConcern的7种写入安全机制抛出异常的级别：
				                   * NONE:没有异常抛出
				                   * NORMAL:仅抛出网络错误异常，没有服务器错误异常，写入到网络就返回
				                   * SAFE:抛出网络错误异常、服务器错误异常；并等待服务器完成写操作。
				                   * MAJORITY: 抛出网络错误异常、服务器错误异常；并多数主服务器完成写操作。
				                   * FSYNC_SAFE: 抛出网络错误异常、服务器错误异常；写操作等待服务器将数据刷新到磁盘。
				                   * JOURNAL_SAFE:抛出网络错误异常、服务器错误异常；写操作等待服务器提交到磁盘的日志文件。
				                   * REPLICAS_SAFE:抛出网络错误异常、服务器错误异常；等待至少2台服务器完成写操作。
				                   * */
				                  .writeConcern(WriteConcern.JOURNALED)
								  .build()
		);
	}
}