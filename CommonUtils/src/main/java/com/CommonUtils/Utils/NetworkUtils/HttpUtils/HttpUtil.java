package com.CommonUtils.Utils.NetworkUtils.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

import com.CommonUtils.Utils.IOUtils.IOUtil;
import com.CommonUtils.Utils.NetworkUtils.HttpUtils.Bean.DownloadFileInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpUtil 
{
	private HttpUtil() {}
	
	/**
	 * 生成下载响应信息
	 * 
	 * 由于一个奇葩的浏览器————IE的存在，响应的时候需要对它单独处理，同时响应给用户的文件名中有可能包含一些不是英文和数字的字符，如汉语，也需要进行处理
	 * */
	public static ResponseEntity<Resource> downloadResponse(final DownloadFileInfo fileInfo) throws UnsupportedEncodingException, MalformedURLException
	{
		File file = fileInfo.getFile();
        String fileName = fileInfo.getFileName();
        //Resource body = new FileSystemResource(file);
        Resource body = new UrlResource(file.toURI());
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String header = request.getHeader("User-Agent").toUpperCase();
        HttpStatus status = HttpStatus.CREATED;
        
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) 
        {
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            //fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
        	fileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
            status = HttpStatus.OK;
        } 
        else 
        {
        	//fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        	fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        	
        	//这句不确定要不要加
        	//status = HttpStatus.CREATED;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(file.length());

        return new ResponseEntity<Resource>(body, headers, status);
	}
	
	public static ResponseEntity<byte[]> downloadResponse(final byte[] datas, final String paramFileName)
	{        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String header = request.getHeader("User-Agent").toUpperCase();
        HttpStatus status = HttpStatus.CREATED;
        
        String fileName = paramFileName;
        
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) 
        {
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            //fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
        	fileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
            status = HttpStatus.OK;
        } 
        else 
        {
        	//fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        	fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        	
        	//这句不确定要不要加
        	//status = HttpStatus.CREATED;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(datas.length);

        return new ResponseEntity<byte[]>(datas, headers, status);
	}
	
	/**
	 * HttpServletResponse回写信息给前端，其中contentType由MediaType提供
	 * */
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
		{ IOUtil.closeQuietly(writer); }
	}
	
	/**
	 * 当前的HTTP Session也可以通过原始Servlet API以编程方式获得：
	 * */
	public static Optional<HttpSession> getHttpSession(final boolean allowCreateSession)
	{
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpSession session= attr.getRequest().getSession(allowCreateSession); // true == allow create
		return Optional.ofNullable(session);
	}
	
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
		{ httpURLConnection.setRequestProperty("Content-Type", contentType); }
		
		if (keepAlive)
		{ httpURLConnection.setRequestProperty("Connection", "Keep-Alive"); }
		
		httpURLConnection.setUseCaches(useCaches);
		httpURLConnection.setDoOutput(doOutput);
		httpURLConnection.setDoInput(doInput);
		return httpURLConnection;
	}
	
	/**
	 * 获取@Scope("session")的bean
	 * */
	public static Optional<Object> getBean(final HttpSession httpSession, final String beanName)
	{ return Optional.ofNullable(httpSession.getAttribute(beanName)); }
	
	/**
	 * 注入@Scope("session")的bean
	 * */
	public static void setBean(final HttpSession httpSession, final String beanName, final Object beanInstance)
	{ httpSession.setAttribute(beanName, beanInstance); }
	
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
		boolean isforward = false;
		try 
		{
			if (isAjaxRequest(request))
			{
				request.getRequestDispatcher(url).forward(request, response); // AJAX不支持Redirect改用Forward
				isforward = true;
			}
			else
			{
				response.sendRedirect(request.getContextPath() + url);
				isforward = false;
			}
		} 
		catch (Exception ex) 
		{ log.error("{}网页出现异常，网页路径为：{}，异常原因为：", isforward ? "转发" : "重定向", url, ex); }
	}
	
	/**
	 * 是否是Ajax异步请求
	 * @param request
	 */
	public static boolean isAjaxRequest(HttpServletRequest request)
	{
		String accept = request.getHeader(HttpHeaders.ACCEPT.toLowerCase());
		if (accept != null && accept.indexOf("application/json") != -1) { return true; }

		String xRequestedWith = request.getHeader(com.CommonUtils.Utils.NetworkUtils.HttpUtils.HttpHeaders.X_REQUESTED_WITH);
		if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) { return true; }
		
		return false;
	}
}