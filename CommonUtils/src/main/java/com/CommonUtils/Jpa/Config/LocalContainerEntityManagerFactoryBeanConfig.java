package com.CommonUtils.Jpa.Config;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.CommonUtils.Utils.CollectionUtils.CustomCollections.HashMap;

/**
 * JpaProperties请用@Resource引用
 * PersistenceUnitManager请用@Autowired(required = false)引用
 * 
 * */
public final class LocalContainerEntityManagerFactoryBeanConfig 
{
	private LocalContainerEntityManagerFactoryBeanConfig() {}
	
	public static LocalContainerEntityManagerFactoryBean getInstance(final JpaProperties jpaProperties,
																	 final String packagesToScan,
																	 final String persistenceUnit,
																	 final EntityManagerFactoryBuilder builder,
																	 final DataSource dataSource)
	{
		
		return builder.dataSource(dataSource)
					  //.properties(getVendorProperties(dataSource, jpaProperties, database))
					  .packages(packagesToScan)
					  .persistenceUnit(persistenceUnit)
					  .build();
	}
	
	public static JpaTransactionManager getJpaTransactionManager(final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean)
	{ return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject()); }
	
	public static EntityManager getEntityManager(final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) 
	{ return localContainerEntityManagerFactoryBean.getObject().createEntityManager(); }
	
	public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(final PersistenceUnitManager persistenceUnitManager, 
																			 final JpaProperties jpaProperties,
																			 final Database database,
																			 final DataSource dataSource)
	{
		return new EntityManagerFactoryBuilder
			   (
					   new HibernateJpaVendorAdapter(), 
					   getVendorProperties(dataSource, jpaProperties, database), 
					   persistenceUnitManager
			   );
	}
	
	private static Map<String, String> getVendorProperties(final DataSource dataSource,
												           final JpaProperties jpaProperties,
												           final Database database)
	{
		Map<String, String> properties = new HashMap<String, String>()
				.put("hibernate.hbm2ddl.auto", "update")
				.getMap();

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