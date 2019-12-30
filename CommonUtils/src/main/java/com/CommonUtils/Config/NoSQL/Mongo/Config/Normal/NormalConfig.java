package com.CommonUtils.Config.NoSQL.Mongo.Config.Normal;

import java.util.Map;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

import com.CommonUtils.Config.NoSQL.Mongo.Config.MongoClientSettingsConfig;
import com.mongodb.MongoCredential;

public final class NormalConfig 
{
	private NormalConfig() {}
	
	public static com.mongodb.client.MongoClient getMongoClient(final Map<String, Integer> hostAndPorts, final MongoCredential credential)
	{ return com.mongodb.client.MongoClients.create(MongoClientSettingsConfig.getInstance(hostAndPorts, credential)); }
	
	public static MongoDbFactory getMongoDbFactory(final com.mongodb.client.MongoClient mongoClient, final String databaseName)
	{ return new SimpleMongoClientDbFactory(mongoClient, databaseName); }
	
	public static MongoTemplate getMongoTemplate(final MongoDbFactory mongoDbFactory)
	{ return new MongoTemplate(mongoDbFactory); }
	
	public static MongoTransactionManager getMongoTransactionManager(final MongoDbFactory dbFactory)
	{ return new MongoTransactionManager(dbFactory); }
}