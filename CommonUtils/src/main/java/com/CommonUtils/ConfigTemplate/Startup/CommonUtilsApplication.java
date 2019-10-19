package com.CommonUtils.ConfigTemplate.Startup;

import org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableAutoConfiguration
(
		exclude= 
		{
				DataSourceAutoConfiguration.class, 
				XADataSourceAutoConfiguration.class,
				
				DruidDataSourceAutoConfigure.class,
				
				DynamicDataSourceAutoConfiguration.class,
				
				ThymeleafAutoConfiguration.class,
				
				ShiroAutoConfiguration.class,
				ShiroAnnotationProcessorAutoConfiguration.class,
				
				ConnectionFactoryAutoConfiguration.class
		}
)
@ComponentScan(basePackages= { "com.CommonUtils" })
@IntegrationComponentScan("com.CommonUtils")
@EnableIntegration
@Slf4j
public class CommonUtilsApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(CommonUtilsApplication.class, args);
		
		/**
		 * Java 语言提供一种 ShutdownHook（钩子）进制，当 JVM 接受到系统的关闭通知之后，调用 ShutdownHook 内的方法，用以完成清理操作，从而平滑的退出应用。
		 * 
		 * Runtime.getRuntime().addShutdownHook(Thread) 需要传入一个线程对象，后续动作将会在该异步线程内完成。除了主动关闭应用（使用 kill -15 指令）,以下场景也将会触发 ShutdownHook :

代码执行结束，JVM 正常退出
应用代码中调用 System#exit 方法
应用中发生 OOM 错误，导致 JVM 关闭
终端中使用 Ctrl+C(非后台运行)


相关注意点#
ShutdownHook 代码实现起来相对简单，但是我们还是需要小心下面这些坑。

Runtime.getRuntime().addShutdownHook(Thread) 可以被多次调用

我们可以多次调用 Runtime.getRuntime().addShutdownHook(Thread) 方法，从而增加多个。但是需要注意的是，多个 ShutdownHook 之间并无任何顺序，Java 并不会按照加入顺序执行，反而将会并发执行。

所以尽量在一个 ShutdownHook 完成所有操作。

ShutdownHook 需要尽快执行结束

不要在 ShutdownHook 执行需要被阻塞代码，如 I/0 读写，这样就会导致应用短时间不能被关闭。

Copy
 Runtime.getRuntime().addShutdownHook(new Thread(() -> {
           while (true){
               System.out.println("关闭应用，释放资源");
           }
        }));
上面代码中，我们使用 while(true) 模拟长时间阻塞这种极端情况，关闭该应用时，应用将会一直阻塞在 while代码中，导致应用没办法被关闭。

除了阻塞之外，还需要小心其他会让线程阻塞的行为，比如死锁。

为了避免 ShutdownHook 线程被长时间阻塞，我们可以引入超时进制。如果等待一定时间之后，ShutdownHook 还未完成，由脚本直接调用 kill -9 强制退出或者 ShutdownHook 代码中引入超时进制。
		 * */
		RuntimeUtil.addShutdownHook(() -> { log.info("关闭应用，释放资源"); });
	}
}