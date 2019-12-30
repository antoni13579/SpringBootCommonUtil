package com.CommonUtils.Config.NoSQL.Mongo.Config.Reactive;

import java.util.Map;

import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;

import com.CommonUtils.Config.NoSQL.Mongo.Config.MongoClientSettingsConfig;
import com.mongodb.MongoCredential;

public final class ReactiveConfig 
{
	private ReactiveConfig() {}
	
	public static com.mongodb.reactivestreams.client.MongoClient getReactiveMongoClient(final Map<String, Integer> hostAndPorts, final MongoCredential credential)
	{ return com.mongodb.reactivestreams.client.MongoClients.create(MongoClientSettingsConfig.getInstance(hostAndPorts, credential)); }
	
	public static ReactiveMongoDatabaseFactory getReactiveMongoDatabaseFactory(final com.mongodb.reactivestreams.client.MongoClient mongoClient, final String databaseName)
	{ return new SimpleReactiveMongoDatabaseFactory(mongoClient, databaseName); }
	
	public static ReactiveMongoTemplate getReactiveMongoTemplate(final ReactiveMongoDatabaseFactory databaseFactory)
	{ return new ReactiveMongoTemplate(databaseFactory); }
	
	public static ReactiveMongoTransactionManager getReactiveMongoTransactionManager(final ReactiveMongoDatabaseFactory databaseFactory)
	{ return new ReactiveMongoTransactionManager(databaseFactory); }
}