package com.CommonUtils.Jpa.Config;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;

import com.CommonUtils.Utils.CollectionUtils.CustomCollections.HashMap;

public final class LocalContainerEntityManagerFactoryBeanConfig 
{
	private LocalContainerEntityManagerFactoryBeanConfig() {}
	
	public static LocalContainerEntityManagerFactoryBean getInstance(final EntityManagerFactoryBuilder builder,
																	 final DataSource dataSource,
																	 final JpaProperties jpaProperties,
																	 final Database database,
																	 final String packagesToScan,
																	 final String persistenceUnit)
	{
		return builder.dataSource(dataSource)
					  .properties(getInstance(dataSource, jpaProperties, database))
					  .packages(packagesToScan)
					  .persistenceUnit(persistenceUnit)
					  .build();
	}
	
	public static JpaTransactionManager getJpaTransactionManager(final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean)
	{ return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject()); }
	
	public EntityManager getEntityManager(final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) 
	{ return localContainerEntityManagerFactoryBean.getObject().createEntityManager(); }
	
	private static Map<String, String> getInstance(final DataSource dataSource,
												   final JpaProperties jpaProperties,
												   final Database database)
	{
		Map<String, String> properties = new HashMap<String, String>()
				.put("hibernate.hbm2ddl.auto", "update").getMap();

		jpaProperties.setShowSql(true);
		jpaProperties.setProperties(properties);
		//jpaProperties.setOpenInView(openInView);
		jpaProperties.setGenerateDdl(true);
		//jpaProperties.setDatabasePlatform(databasePlatform);
		jpaProperties.setDatabase(database);
		jpaProperties.determineDatabase(dataSource);
		return jpaProperties.getProperties();
	}
}