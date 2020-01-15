package com.CommonUtils.ConfigTemplate.Config;

import java.time.ZoneId;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.annotation.Aspect;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.CommonUtils.Config.SQL.Jdbc.Config.TxAdviceAdvisorConfig;
import com.CommonUtils.Config.SQL.Jdbc.Config.DataSources.HikariDataSourceConfig;
import com.CommonUtils.Config.SQL.Mybatis.Config.SqlSessionFactoryConfig;
import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.DBUrlUtil;
import com.CommonUtils.Utils.DBUtils.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Utils.DBUtils.Bean.Url.MySqlUrl;

@EnableTransactionManagement
@Aspect
@Configuration
@MapperScan(basePackages = {"com.CommonUtils.MyBatis.MySql.MyDb1.Dao"},
            sqlSessionTemplateRef = "mydb1SqlSessionTemplate")
public class MyBatisConfiguration 
{
	@Bean(name = "mydb1DataSource", destroyMethod = "close")
    //@Primary
    public DataSource mydb1DataSource()
    {
        return HikariDataSourceConfig.getDataSource
        (
                new DBInfo
                (
                        DBContants.MySqlJdbcDriver.MYSQL_NEW.getJdbcDriver(),
                        DBUrlUtil.getMySqlUrl
                        (
                        		new MySqlUrl()
                        			.setAllowPublicKeyRetrieval(true)
                        			.setAutoReconnect(true)
                        			.setCharacterEncoding("utf8")
                        			.setDataBase("mydb1")
                        			.setDefaultFetchSize(DBContants.IN_SQL_STATEMENT_FOR_MYSQL)
                        			.setHostIp("localhost")
                        			.setPort(3306)
                        			.setServerTimezone(ZoneId.SHORT_IDS.get("CTT"))
                        			.setUseCursorFetch(true)
                        			.setUseSSL(true)
                        			.setUseUnicode(true)
                        ),
                        "root",
                        "root",
                        true
                ),
                "mydb1DataSource",
                false
        );
    }
	
	/**
	 * 可选，MyBatis配置为批量处理，JdbcTemplate重要性就不那么大了
	 * */
	@Bean(name = "mydb1JdbcTemplate")
    public JdbcTemplate mydb1JdbcTemplate(@Qualifier("mydb1DataSource")DataSource mydb1DataSource)
    { return new JdbcTemplate(mydb1DataSource); }
	
	@Bean
    public DataSourceTransactionManager mydb1DataSourceTransactionManager(@Qualifier("mydb1DataSource")DataSource mydb1DataSource)
    { return new DataSourceTransactionManager(mydb1DataSource); }
	
	@Bean
	public TransactionTemplate mydb1TransactionTemplate(@Qualifier("mydb1DataSourceTransactionManager")DataSourceTransactionManager mydb1DataSourceTransactionManager)
	{ return new TransactionTemplate(mydb1DataSourceTransactionManager); }
	
	@Bean(name = "mydb1TxAdviceAdvisor")
    public Advisor mydb1TxAdviceAdvisor(@Qualifier("mydb1DataSourceTransactionManager")DataSourceTransactionManager mydb1DataSourceTransactionManager)
    {
        return TxAdviceAdvisorConfig.getInstance
        (
                TxAdviceAdvisorConfig.getAopPointcutExpression("com.CommonUtils.MyBatis.MySql.MyDb1.Dao",
                                                               "com.CommonUtils.MyBatis.MySql.MyDb1.Service"),
                mydb1DataSourceTransactionManager
        );
    }
	
	@Bean(name = "mydb1SqlSessionFactory")
	@Primary
    public SqlSessionFactory mydb1SqlSessionFactory(@Qualifier("mydb1DataSource")DataSource mydb1DataSource) throws Exception
    {
        return SqlSessionFactoryConfig.getInstance
        (
        		mydb1DataSource,
                "com.CommonUtils.MyBatis.MySql.MyDb1.Dao.Mapper"
        );
    }
	
	/**
	 * 可选，这个玩意很少用到
	 * */
	@Bean(name = "mydb1SqlSessionTemplate")
    public SqlSessionTemplate mydb1SqlSessionTemplate(@Qualifier("mydb1SqlSessionFactory")SqlSessionFactory mydb1SqlSessionFactory)
    { return new SqlSessionTemplate(mydb1SqlSessionFactory); }
}