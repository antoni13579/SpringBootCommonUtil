# 基于SpringBoot的通用型工具包 

#### 项目介绍
基于SpringBoot2进行封装的通用型工具类，包含配置类、工具类等

#### 软件架构
软件架构说明

* `com.CommonUtils.CommonPools` 自定义连接池，目前是放进了基于JSCH框架的连接池，<br />
  基于Apache的common pool2框架进行封装。主要是针对SSH、SFTP这种类型的连接做好资源管控功能。

    >（1）`com.CommonUtils.CommonPools.Definition.JschSessionPool.java`<br />定义了整个JSCH连接池设计<br /> <br />
    >（2）`com.CommonUtils.CommonPools.Config.JschSessionPoolConfig.java`<br />定义了当需要使用create bean操作的时候，会提供一种默认方式创建，默认设置为最大5个连接数，其中2个连接数闲置，建议连接数别设置过大，根据经验来说，连接数过大会导致报错Connection reset异常

<br />
<br />

* `com.CommonUtils.Jdbc`主要是封装了数据库连接池、事务管理器AOP触发规则等

    >（1）`com.CommonUtils.Jdbc.Bean.DBBaseInfo.AbstractDBInfo.java`<br />属于DBInfo.java与DBInfoForDataSource.java的抽象类，包装了需要执行的SQL与对应的绑定变量<br /> <br />
    >（2）`com.CommonUtils.Jdbc.Bean.DBBaseInfo.DBInfo.java`<br />这个类用于包装jdbc驱动名称、jdbc地址，用户名与密码，创建数据库连接池或使用流模式读取数据会用上<br /> <br />
    >（3）`com.CommonUtils.Jdbc.Bean.DBBaseInfo.DBInfoForDataSource.java`<br />这个类用于包装数据库连接池与JdbcTemplate实例，使用流模式读取数据会用上<br /> <br />
    >（4）`com.CommonUtils.Jdbc.Bean.Url.*.java`<br />这个包下所有的类，分别对应不同的数据库，都是根据给定参数，包装出jdbc地址需要的信息，然后通过`com.CommonUtils.Utils.DBUtils.DBUrlUtil`工具类生成jdbc地址<br /> <br />
    >（5）`com.CommonUtils.Jdbc.DataSources.HikariDataSourceConfig.java`<br />这个是Hikari数据库连接池默认创建方式，当需要create bean操作的时候可以用上<br /> <br />
    >（6）`com.CommonUtils.Jdbc.DataSources.TomcatOldDataSourceConfig.java`<br />这个是使用Tomcat的DBCP数据库连接池，当需要create bean操作的时候可以用上，一般是用于JDBC驱动比较老的情况，当时项目用的是Teradata，提供的数据库驱动相对老旧，不能使用Hikari，只能用这个，不过建议别用在版本太新的TOMCAT上面，因为新版本不支持老的JDBC驱动，会导致这个数据库连接池报错<br /> <br />
    >（7）`com.CommonUtils.Jdbc.TransactionAttributeConfig.java`<br />这个类包装了配置针对哪些方法进行事务管理，已经封装了相对常用的，如方法名前缀为batchInsert，find等，这个类不需要自己去调用，是由`com.CommonUtils.Jdbc.TxAdviceAdvisorConfig.java`类来触发即可<br /> <br />
    >（8）`com.CommonUtils.Jdbc.TxAdviceAdvisorConfig.java`<br />这个类针对哪些包里面的Dao方法或Service方法进行事务拦截处理<br /> <br />

<br />
<br />

* `com.CommonUtils.Kafka`主要是封装了Kafka生产者、消费者配置，Kafka-Stream基于LowLevel的处理模板

    >（1）`com.CommonUtils.Kafka.Bean.KafkaMessage`<br />包装的Kafka消息数据<br /> <br />
    >（2）`com.CommonUtils.Kafka.Stream.Config.LowLevel.*.java`<br />这个是Kafka-Stream基于LowLevel的相关操作工具，不过目测现在都是用Spark为主，这里不做过多介绍，有兴趣可以看源码<br /> <br />
    >（3）`com.CommonUtils.Kafka.ConsumerRelatedConfig.java`<br />这个是Kafka消费者默认配置，需要与create bean搭配使用<br /> <br />
    >（4）`com.CommonUtils.Kafka.ProducerRelatedConfig.java`<br />这个是Kafka生产者默认配置，需要与create bean搭配使用<br /> <br />

<br />
<br />

* `com.CommonUtils.MiddleWare`主要是封装了对中间件配置类，目前主要是以TOMCAT为主

    >（1）`com.CommonUtils.MiddleWare.GracefulShutdowns.GracefulShutdownTomcat.java`<br />针对优雅关闭Tomcat的需求，包装了一套配置类<br /> <br />
    >（2）`com.CommonUtils.MiddleWare.ServletWebServerFactoryConfig.java`<br />针对Tomcat底层参数做配置，如http跳转至https功能；配置最大连接数、最大线程数等<br /> <br />
    >（3）目前上述两个类已经做了自动化配置，路径在`com.CommonUtils.Web.WebConfiguration.java`<br /> <br />

<br />
<br />

* `com.CommonUtils.Mybatis`MyBatis的底层配置

    >（1）`com.CommonUtils.Mybatis.Config.MybatisBaseConfig.java`<br />生成MyBatis基础配置，生成会话工厂的时候会用上，一般不需要自己调用<br /> <br />
    >（2）`com.CommonUtils.Mybatis.Config.PageConfig.java`<br />MyBatis有一个分页插件是PageHelper，针对这个东西做配置，生成会话工厂的时候会用上，一般不需要自己调用<br /> <br />
    >（3）`com.CommonUtils.Mybatis.Config.SqlSessionFactoryConfig.java`<br />MyBatis的会话工厂配置，需要与create bean搭配使用<br /> <br />
    >（4）`com.CommonUtils.Mybatis.Log.Impl.MyBatisSqlLogBaseImpl.java`<br />MyBatis日志输入，级别为基本日志<br /> <br />
    >（5）`com.CommonUtils.Mybatis.Log.Impl.MyBatisSqlLogDetailImpl.java`<br />MyBatis日志输入，级别为详细日志<br /> <br />

<br />
<br />

* `未完待续`

#### 安装教程

1. 无需安装，使用ide软件即可打开，只是说需要导出为jar包，放到maven仓库供其他项目使用

#### 使用说明

1. 若需要使用此工具类，CommonUtil的maven依赖，如Jsch，common-poo2这些框架也需要在调用方也存在，否则会包ClassNotFoundException异常