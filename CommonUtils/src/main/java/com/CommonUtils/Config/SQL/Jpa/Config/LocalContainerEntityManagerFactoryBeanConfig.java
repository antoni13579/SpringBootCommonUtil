package com.CommonUtils.Config.SQL.Jpa.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 *PersistenceUnitManager请用@Autowired(required = false)引用
 *JpaProperties请用@Resource引用 
 * 建议使用mybatis plus
 * @deprecated
 * */ 
@Deprecated(since="建议使用mybatis plus")
public final class LocalContainerEntityManagerFactoryBeanConfig 
{
	private LocalContainerEntityManagerFactoryBeanConfig() {}
	
	public static LocalContainerEntityManagerFactoryBean getInstance(final String packagesToScan,
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
	{
		EntityManagerFactory entityManagerFactory = localContainerEntityManagerFactoryBean.getObject();
		return new JpaTransactionManager(Optional.ofNullable(entityManagerFactory).orElseThrow());
	}
	
	public static EntityManager getEntityManager(final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) 
	{
		EntityManagerFactory entityManagerFactory = localContainerEntityManagerFactoryBean.getObject();
		return Optional.ofNullable(entityManagerFactory).orElseThrow().createEntityManager();
	}
	
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
		Map<String, String> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");

		jpaProperties.setShowSql(true);
		jpaProperties.setProperties(properties);
		jpaProperties.setGenerateDdl(true);
		jpaProperties.setDatabase(database);
		jpaProperties.determineDatabase(dataSource);
		return jpaProperties.getProperties();
	}
}