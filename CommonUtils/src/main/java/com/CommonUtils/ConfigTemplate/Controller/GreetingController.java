package com.CommonUtils.ConfigTemplate.Controller;

import java.security.Principal;

import javax.annotation.Resource;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import com.CommonUtils.ConfigTemplate.Bean.ClientMessage;
import com.CommonUtils.ConfigTemplate.Bean.ServerMessage;

@Controller
public class GreetingController 
{
	@Resource
    private SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public ServerMessage greeting(ClientMessage message, Principal principal) throws Exception 
	{ return new ServerMessage("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"); }
}