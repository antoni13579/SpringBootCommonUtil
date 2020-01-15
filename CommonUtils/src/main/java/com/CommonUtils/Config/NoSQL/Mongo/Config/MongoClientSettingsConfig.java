package com.CommonUtils.Config.NoSQL.Mongo.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public final class MongoClientSettingsConfig 
{
	private MongoClientSettingsConfig() {}
	
	public static MongoClientSettings getInstance(final Map<String, Integer> hostAndPorts, final MongoCredential credential)
	{
		return MongoClientSettings.builder()
		                          //.addCommandListener(commandListener)
								  //.applicationName(applicationName)
                                  //.applyConnectionString(connectionString)

								  .applyToClusterSettings
								  (
										  block -> 
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
							     			  block.hosts(seeds);
							     		  }
								  )
        
								  .applyToConnectionPoolSettings
								  (
										  block -> block.minSize(10)
											  	   		.maxSize(40)
											  	   		.maxWaitQueueSize(40)
											  	   		.maxWaitTime(4, TimeUnit.MINUTES)
											  	   		.maxConnectionLifeTime(30, TimeUnit.MINUTES)
											  	   		.maxConnectionIdleTime(10, TimeUnit.MINUTES)
											  	   		.maintenanceFrequency(30, TimeUnit.MINUTES)
											  	   		.maintenanceInitialDelay(1, TimeUnit.HOURS)
								  )
								  
								  .applyToServerSettings
								  (
										  block -> block.heartbeatFrequency(10, TimeUnit.SECONDS)
											  	   		.minHeartbeatFrequency(3, TimeUnit.SECONDS)
								  )
								  
								  .applyToSocketSettings
								  (
										  block -> block.connectTimeout(2, TimeUnit.MINUTES)
								  )
								  
								  .applyToSslSettings
								  (
										  block -> block.enabled(false)
											  	   		.invalidHostNameAllowed(false)
								  )
								  
								  //.autoEncryptionSettings(autoEncryptionSettings)
								  //.codecRegistry(codecRegistry)
								  //.commandListenerList(commandListeners)
								  //.compressorList(compressorList)
        
								  .credential(credential)
        
								  //.readConcern(readConcern)
								  .readPreference(ReadPreference.secondaryPreferred())
								  .retryReads(true)
								  
								  //.retryWrites(retryWrites)
								  //.streamFactoryFactory(streamFactoryFactory)
								  .writeConcern(WriteConcern.JOURNALED)
								  .build();
	}
}