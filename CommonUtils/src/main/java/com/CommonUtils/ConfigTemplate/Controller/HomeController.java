package com.CommonUtils.ConfigTemplate.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/homeController")
public class HomeController 
{
	@RequestMapping("/login")
	public String login()
	{ return "/login.html"; }
	
	@RequestMapping("/webSocketPage")
	public String webSocketPage()
	{ return "/WebSocketPage.html"; }
}