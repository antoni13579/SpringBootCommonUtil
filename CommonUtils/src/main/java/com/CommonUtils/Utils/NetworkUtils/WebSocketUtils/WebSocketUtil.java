package com.CommonUtils.Utils.NetworkUtils.WebSocketUtils;

import java.util.Optional;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class WebSocketUtil 
{
	private WebSocketUtil() {}
	
	/**
	 * 创建StompSession后，使用session.send发送消息，如发送/app/hello
	 * 
	 * */
	public static Optional<StompSession> getStompSession(final String webSocketUrl, 
														 final String subscribeTopic,
														 final StompFrameHandler stompFrameHandler)
	{
		StompSession session = null;
        try
        {         
            WebSocketTransport transport = new WebSocketTransport(new StandardWebSocketClient());
            WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(CollUtil.newArrayList(transport)));
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            session = stompClient.connect(webSocketUrl, new StompSessionHandlerAdapter() {}).get();
            session.subscribe(subscribeTopic, stompFrameHandler);
        }
        catch (Exception ex)
        { log.error("获取WebSocket会话出现异常，异常原因为：", ex); }
        
        return Optional.ofNullable(session);
	}
	
	public static void convertAndSendToUser(final SimpMessagingTemplate simpMessagingTemplate, final String user, final String destination, final Object msg)
	{ Optional.ofNullable(simpMessagingTemplate).ifPresent(consumer -> consumer.convertAndSendToUser(user, destination, msg)); }
}