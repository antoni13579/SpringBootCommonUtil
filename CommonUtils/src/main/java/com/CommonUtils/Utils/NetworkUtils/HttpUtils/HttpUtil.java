package com.CommonUtils.Utils.NetworkUtils.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpUtil 
{
	private HttpUtil() {}
	
	/**建议cn.hutool.extra.servlet.ServletUtil.write\
	 * @deprecated
	 * */
	@Deprecated(since="建议cn.hutool.extra.servlet.ServletUtil.write")
	private static Map<String, Object> downloadResponse(final long contentLength, final String paramFileName)
	{
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		
		HttpServletRequest request = Optional.ofNullable(servletRequestAttributes).orElseThrow().getRequest();
        String header = request.getHeader(Header.USER_AGENT.toString()).toUpperCase();
        HttpStatus status = null;
        
        String fileName = paramFileName;
        
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) 
        {
        	fileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
            status = HttpStatus.OK;
        } 
        else 
        {
        	fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        	status = HttpStatus.CREATED;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(contentLength);
        
        Map<String, Object> map = new HashMap<>();
        map.put("HEADERS", headers);
        map.put("STATUS", status);
        return map;
	}
	
	/**
	 * 生成下载响应信息，由于一个奇葩的浏览器————IE的存在，响应的时候需要对它单独处理，同时响应给用户的文件名中有可能包含一些不是英文和数字的字符，如汉语，也需要进行处理 
	 * 建议cn.hutool.extra.servlet.ServletUtil.write
	 * @deprecated
	 * */
	@Deprecated(since="建议cn.hutool.extra.servlet.ServletUtil.write")
	public static ResponseEntity<Resource> downloadResponse(final com.CommonUtils.Utils.NetworkUtils.HttpUtils.Bean.DownloadFileInfo fileInfo) throws MalformedURLException
	{
		File file = fileInfo.getFile();
        Resource body = new UrlResource(file.toURI());        
        Map<String, Object> configResult = downloadResponse(file.length(), fileInfo.getFileName());
        return new ResponseEntity<>(body, (HttpHeaders)configResult.get("HEADERS"), (HttpStatus)configResult.get("STATUS"));
	}
	
	/**建议cn.hutool.extra.servlet.ServletUtil.write
	 * @deprecated
	 * */
	@Deprecated(since="建议cn.hutool.extra.servlet.ServletUtil.write")
	public static ResponseEntity<byte[]> downloadResponse(final byte[] datas, final String paramFileName)
	{        
        Map<String, Object> configResult = downloadResponse(datas.length, paramFileName);
        return new ResponseEntity<>(datas, (HttpHeaders)configResult.get("HEADERS"), (HttpStatus)configResult.get("STATUS"));
	}
	
	/**
	 *HttpServletResponse回写信息给前端，其中contentType由MediaType提供 
	 * 建议使用cn.hutool.extra.servlet.ServletUtil.write相关方法
	 * @deprecated
	 * */
	@Deprecated(since="建议使用cn.hutool.extra.servlet.ServletUtil.write相关方法")
	public static void responseInfo(final HttpServletResponse response, 
									final String str, 
									final MediaType mediaType)
	{		
		PrintWriter writer = null;
		try
		{
			response.setContentType(mediaType.toString());
			writer = response.getWriter();
			writer.write(str);
			writer.flush();
		}
		catch (Exception ex)
		{ log.error("返回字符串信息给页面出现异常，异常原因为：", ex); }
		finally
		{ IoUtil.close(writer); }
	}
	
	/**
	 * 当前的HTTP Session也可以通过原始Servlet API以编程方式获得
	 * @deprecated
	 * */
	@Deprecated(since="当前的HTTP Session也可以通过原始Servlet API以编程方式获得")
	public static HttpSession getHttpSession(final boolean allowCreateSession)
	{ return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest().getSession(allowCreateSession); }
	
	/**建议使用cn.hutool.http.HttpConnection
	 * @deprecated
	 * */
	@Deprecated(since="cn.hutool.http.HttpConnection")
	public static HttpURLConnection getHttpURLConnection(final String urlPath, 
														 final RequestMethod requestMethod,
														 final String contentType,
														 final boolean keepAlive,
														 final boolean useCaches,
														 final boolean doOutput,
														 final boolean doInput) throws IOException
	{
		URL url = new URL(urlPath);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod(requestMethod.name().toUpperCase());
		
		if (!StringUtil.isStrEmpty(contentType))
		{ httpURLConnection.setRequestProperty(Header.CONTENT_TYPE.toString(), contentType); }
		
		if (keepAlive)
		{ httpURLConnection.setRequestProperty(Header.CONNECTION.toString(), "Keep-Alive"); }
		
		httpURLConnection.setUseCaches(useCaches);
		httpURLConnection.setDoOutput(doOutput);
		httpURLConnection.setDoInput(doInput);
		return httpURLConnection;
	}
	
	/**
	 * 支持AJAX的页面跳转
	 * 
	 * <p>特殊情况：html与jsp并存，主力军html，辅助军jsp</p>
	 * 
	 * <p>jsp存放路径：src/main目录下，建立webapp/WEB-INF目录，接下来就可以存放jsp页面文件了</p>
	 * <p>访问路径：假设jsp文件存放的路径如下：src/main/webapp/WEB-INF/jsp/testJsp.jsp</p>
	 * <p>那么url就需要输入为：/WEB-INF/jsp/testJsp.jsp</p>
	 * 
	 */
	public static void redirectUrl(HttpServletRequest request, HttpServletResponse response, String url)
	{
		try 
		{
			if (isAjaxRequest(request))
			{ request.getRequestDispatcher(url).forward(request, response); }
			else
			{ response.sendRedirect(request.getContextPath() + url); }
		} 
		catch (Exception ex) 
		{ log.error("重定向或转发网页出现异常，网页路径为：{}，异常原因为：", url, ex); }
	}
	
	/**
	 * 是否是Ajax异步请求
	 * @param request
	 */
	public static boolean isAjaxRequest(HttpServletRequest request)
	{
		String accept = request.getHeader(HttpHeaders.ACCEPT.toLowerCase());
		if (accept != null && accept.indexOf(ContentType.JSON.toString()) != -1) { return true; }

		String xRequestedWith = request.getHeader("X-Requested-With");
		return xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1;
	}
}