package com.CommonUtils.ConfigTemplate.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.CommonUtils.Utils.HttpUtils.HttpUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/homeController")
@Api(value = "核心Controller")
public class HomeController 
{
	@RequestMapping("/login")
	@ApiIgnore
	public String login()
	{ return "/login.html"; }
	
	@RequestMapping("/webSocketPage")
	@ApiOperation(value = "WebSocket测试页面",notes = "WebSocket测试页面")
	public String webSocketPage()
	{ return "/WebSocketPage.html"; }
	
	@RequestMapping("/testJsp")
	public void testJsp(final HttpServletRequest request, final HttpServletResponse response)
	{ HttpUtil.jumpPage("/jsp/testJsp.jsp", request, response); }
}