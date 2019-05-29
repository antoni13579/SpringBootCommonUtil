package com.CommonUtils.ConfigTemplate.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer
{
	/**
	 * 表示前缀为/ws/endpointChat的endPoint，并开启sockJs支持，sockjs可以解决
	 * 浏览器对WebSocket的兼容性问题，客户端通过这里配置的URL来建立WebSocket连接，这个连接不建立，WebSocket就无从谈起。
	 * 前端使用如下代码连接到服务器，全面建立WebSocket连接
	 *  var socket = new SockJS('/ws/endpointChat');
	 *  var stompClient = Stomp.over(socket);
	 * */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) 
	{ stompEndpointRegistry.addEndpoint("/ws/endpointChat").withSockJS(); }
	
	/**
	 * 设置前端订阅的地址，像这里设置，要求前端订阅前缀为/topic/xxx的地址，这个规定由registry.enableSimpleBroker定下来，代码例子如下
	 * 
	 * stompClient.connect
	 * (
	 * 			{}, 
	 * 			function (frame) 
	 * 			{
	 * 				setConnected(true);
	 * 				stompClient.subscribe
	 * 				(
	 * 					'/topic/greetings',
	 * 					function (greeting) 
	 * 					{ showGreeting(JSON.parse(greeting.body).content); } 
	 * 				);
	 * 			}
	 * );
	 * 
	 * 1、代码例子写成是/topic/greetings，符合/topic/xxx的要求，服务器会接收响应，这个/topic/greetings会由后台的Controller进行接收处理，也就是GreetingController
	 * 2、除了订阅，还需要定义好服务器返回给客户端的信息应如何处理，处理方法就是showGreeting方法
	 * 
	 * 当客户端发送消息的时候，执行这个代码
	 * stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
	 * 
	 * 1、在GreetingController中，拦截的url为/hello，不过由于设置了registry.setApplicationDestinationPrefixes，因此发送地址必须为/app/hello
	 * 2、GreetingController接收到/app/hello的请求后，把最终处理结果转发给/topic/greetings
	 * 3、由于前端已经订阅了/topic/greetings，所以客户端会接收到服务器返回的信息
	 * */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) 
	{
		registry.enableSimpleBroker("/topic");
		
		//如果不设置这个，默认前缀为/user
		registry.setApplicationDestinationPrefixes("/app");
	}
}