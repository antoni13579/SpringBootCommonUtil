package com.CommonUtils.MiddleWare;

import java.nio.charset.Charset;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

public final class ServletWebServerFactoryConfig 
{
	private ServletWebServerFactoryConfig() {}
	
	/**
	 * 如果部署到Tomcat，请配置maxSwallowSize以避免此Tomcat连接重置问题。 对于嵌入式Tomcat，声明一个TomcatServletWebServerFactory
	 * */
	/**仅配置HTTP*/
	public static TomcatServletWebServerFactory getTomcatInstance(final int httpPort)
	{
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		addConnectorCustomizers(tomcat);
        tomcat.addAdditionalTomcatConnectors(getConnector(httpPort));
		return tomcat;
	}
	
	/**配置http与https，同时配置http跳转到https功能*/
	public static TomcatServletWebServerFactory getTomcatInstance(final int httpPort, final int httpsPort)
	{
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory()
		{
			@Override            
			protected void postProcessContext(Context context) 
			{
				SecurityConstraint constraint = new SecurityConstraint();                
				constraint.setUserConstraint("CONFIDENTIAL");                
				SecurityCollection collection = new SecurityCollection();                
				collection.addPattern("/*");                
				constraint.addCollection(collection);                
				context.addConstraint(constraint);            
			}
		};
		addConnectorCustomizers(tomcat);
        tomcat.addAdditionalTomcatConnectors(getConnector(httpPort, httpsPort));
		return tomcat;
	}
	
	/**TOMCAT底层配置*/
	private static void addConnectorCustomizers(final TomcatServletWebServerFactory tomcat)
	{
		tomcat.addConnectorCustomizers
		(
				(Connector connector) -> 
				{
					//-1 means unlimited
					if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) 
					{
						AbstractHttp11Protocol<?> abstractHttp11Protocol = (AbstractHttp11Protocol<?>)connector.getProtocolHandler();
						abstractHttp11Protocol.setMaxSwallowSize(-1);	 	   //配置maxSwallowSize以避免此Tomcat连接重置问题
						abstractHttp11Protocol.setMaxConnections(2000);  	   //设置最大连接数  
						abstractHttp11Protocol.setMaxThreads(2000);      	   //设置最大线程数  
						abstractHttp11Protocol.setConnectionTimeout(30000);    //连接超时时间
						abstractHttp11Protocol.setMaxHttpHeaderSize(1 * 1024 * 1024); //设置为1MB，防止报出Error parsing HTTP request header
					}
				}
	    );
		
		tomcat.setUriEncoding(Charset.forName("utf8"));
	}
	
	private static Connector getConnector(final int httpPort)
	{
		//配置http
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(httpPort);
        return connector;
	}
	
	private static Connector getConnector(final int httpPort, final int httpsPort)
	{
		//配置http
		Connector connector = getConnector(httpPort);
		connector.setScheme("http");
				
		//需要重定向的http端口
		connector.setSecure(false);
		        
		//设置重定向到https端口
		connector.setRedirectPort(httpsPort);
		
		return connector;
	}
}