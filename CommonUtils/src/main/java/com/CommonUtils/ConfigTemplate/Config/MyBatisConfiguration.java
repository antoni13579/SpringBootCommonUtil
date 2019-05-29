package com.CommonUtils.ConfigTemplate.Config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.CommonUtils.Jdbc.TxAdviceAdvisorConfig;
import com.CommonUtils.Jdbc.Bean.DBBaseInfo.DBInfo;
import com.CommonUtils.Jdbc.Bean.Url.OracleUrl;
import com.CommonUtils.Jdbc.DataSources.HikariDataSourceConfig;
import com.CommonUtils.Mybatis.Config.SqlSessionFactoryConfig;
import com.CommonUtils.Utils.DBUtils.DBContants;
import com.CommonUtils.Utils.DBUtils.DBUrlUtil;

/**
 * 
@EnableTransactionManagement
@Aspect
@Configuration
@MapperScan(basePackages = {"com.ExtractDataToTD.MyBatis.ScsPlus.Edw.Dao"},
            sqlSessionTemplateRef = "scsPlusEdwSqlSessionTemplate")
 * */
public class MyBatisConfiguration 
{
	@Bean(name = "scsPlusEdwDataSource", destroyMethod = "close")
    //@Primary
    public DataSource scsPlusEdwDataSource()
    {
        return HikariDataSourceConfig.getDataSource
        (
                new DBInfo
                (
                        DBContants.ORACLE_JDBC_DRIVER_NEW,
                        DBUrlUtil.getOracleUrl(new OracleUrl()),
                        "username",
                        "password"
                ),
                "scsPlusEdwDataSource",
                false
        );
    }
	
	/**
	 * 可选，MyBatis配置为批量处理，JdbcTemplate重要性就不那么大了
	 * */
	@Bean(name = "scsPlusEdwJdbcTemplate")
    public JdbcTemplate scsPlusEdwJdbcTemplate(@Qualifier("scsPlusEdwDataSource")DataSource scsPlusEdwDataSource)
    { return new JdbcTemplate(scsPlusEdwDataSource); }
	
	@Bean
    public DataSourceTransactionManager scsPlusEdwDataSourceTransactionManager(@Qualifier("scsPlusEdwDataSource")DataSource scsPlusEdwDataSource)
    { return new DataSourceTransactionManager(scsPlusEdwDataSource); }
	
	@Bean(name = "scsPlusEdwTxAdviceAdvisor")
    public Advisor scsPlusEdwTxAdviceAdvisor(@Qualifier("scsPlusEdwDataSourceTransactionManager")DataSourceTransactionManager scsPlusEdwDataSourceTransactionManager)
    {
        return TxAdviceAdvisorConfig.getInstance
        (
                TxAdviceAdvisorConfig.getAopPointcutExpression("com.ExtractDataToTD.MyBatis.ScsPlus.Edw.Dao",
                                                               "com.ExtractDataToTD.MyBatis.ScsPlus.Edw.Service"),
                scsPlusEdwDataSourceTransactionManager
        );
    }
	
	@Bean(name = "scsPlusEdwSqlSessionFactory")
    public SqlSessionFactory scsPlusEdwSqlSessionFactory(@Qualifier("scsPlusEdwDataSource")DataSource scsPlusEdwDataSource) throws Exception
    {
        return SqlSessionFactoryConfig.getInstance
        (
                scsPlusEdwDataSource,
                "com.ExtractDataToTD.MyBatis.ScsPlus.Edw.Dao.Mapper"
        );
    }
	
	/**
	 * 可选，这个玩意很少用到
	 * */
	@Bean(name = "scsPlusEdwSqlSessionTemplate")
    public SqlSessionTemplate scsPlusEdwSqlSessionTemplate(@Qualifier("scsPlusEdwSqlSessionFactory")SqlSessionFactory scsPlusEdwSqlSessionFactory)
    { return new SqlSessionTemplate(scsPlusEdwSqlSessionFactory); }
}