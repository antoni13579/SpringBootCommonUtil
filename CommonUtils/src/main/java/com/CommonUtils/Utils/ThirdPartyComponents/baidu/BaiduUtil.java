package com.CommonUtils.Utils.ThirdPartyComponents.baidu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.HttpUtils.HttpUtil;
import com.CommonUtils.Utils.IOUtils.FileUtil;
import com.CommonUtils.Utils.IOUtils.IOUtil;
import com.CommonUtils.Utils.ThirdPartyComponents.baidu.Bean.VatInvoice;
import com.baidu.aip.util.Base64Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BaiduUtil 
{
	private BaiduUtil() {}
	
	public static String getAuthToken(final String appKey, final String secretKey)
	{
		String token = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream is = null;
		try
		{
			StringBuilder authTockenUrl = new StringBuilder()
					.append(Contants.GET_TOKEN_URL)
					.append("?")
					
					 // 1. grant_type为固定参数
					.append("grant_type=client_credentials")
					
					// 2. 官网获取的 API Key
					.append("&client_id=")
					.append(appKey)
					
					//3. 官网获取的 Secret Key
					.append("&client_secret=")
					.append(secretKey);
			
			// 打开和URL之间的连接
			HttpURLConnection connection = HttpUtil.getHttpURLConnection(authTockenUrl.toString(), RequestMethod.POST, null, false, true, false, true);
            connection.connect();
            
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            JavaCollectionsUtil.mapProcessor
            (
            		map, 
            		(final String key, final List<String> value, final int indx) -> 
            		{ log.debug("申请访问百度API，百度返回的响应头字段信息，{} ------> {}", key, value); }
            );
            
            is = connection.getInputStream();
            inputStreamReader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(inputStreamReader);
            
            String line;
            StringBuilder remoteResult = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) 
            { remoteResult.append(line); }
            
            log.debug("百度API返回的AccessToken相关信息为--------------> {}", remoteResult.toString());
            JSONObject jsonObject = new JSONObject(remoteResult.toString());
            token = jsonObject.getString("access_token");
		}
		catch (Exception ex)
		{
			log.error("获取百度AccessToken出现异常，异常原因为：", ex);
			token = "";
		}
		finally
		{ IOUtil.closeQuietly(is, inputStreamReader, bufferedReader); }
		
		return token;
	}
	
	public static Optional<VatInvoice> getVatInvoice(final Path path, final String accessToken)
	{ return getVatInvoice(path.toFile(), accessToken); }
	
	public static Optional<VatInvoice> getVatInvoice(final String filePath, final String accessToken)
	{ return getVatInvoice(new File(filePath), accessToken); }
	
	public static Optional<VatInvoice> getVatInvoice(final File file, final String accessToken)
	{
		VatInvoice result = null;
		DataOutputStream dataOutputStream = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		try
		{
			byte[] data = FileUtil.toBytes(file).orElseThrow(() -> new Exception("文件转换为字节数组，返回为空！！！"));
			String imgStr = Base64Util.encode(data);
			
			StringBuilder requestUrl = new StringBuilder()
					.append(Contants.VAT_INVOICE_RECOGNITION_URL)
					.append("?access_token=")
					.append(accessToken);
			
			HttpURLConnection connection = HttpUtil.getHttpURLConnection(requestUrl.toString(), RequestMethod.POST, MediaType.APPLICATION_FORM_URLENCODED_VALUE, true, false, true, true);
			
			StringBuilder params = new StringBuilder()
					.append(URLEncoder.encode("image", "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(imgStr, "UTF-8"));
			
			String encoding = requestUrl.toString().contains("nlp") ? "GBK" : StandardCharsets.UTF_8.name();
			
			// 得到请求的输出流对象
			dataOutputStream = new DataOutputStream(connection.getOutputStream());
			dataOutputStream.write(params.toString().getBytes(encoding));
			dataOutputStream.flush();
			
			// 建立实际的连接
	        connection.connect();
	        
	        // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            JavaCollectionsUtil.mapProcessor
            (
            		map, 
            		(final String key, final List<String> value, final int indx) -> 
            		{ log.debug("申请访问百度API的增值税识别接口，百度返回的响应头字段信息，{} ------> {}", key, value); }
            );
            
            // 定义 BufferedReader输入流来读取URL的响应
            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, encoding);
            bufferedReader = new BufferedReader(inputStreamReader);
            
            String getLine;
            StringBuilder json = new StringBuilder();
            while ((getLine = bufferedReader.readLine()) != null) 
            { json.append(getLine); }
            
            result = VatInvoice.getInstance(json.toString()).setFile(file);
		}
		catch (Exception ex)
		{ log.error("获取百度增值税发票识别结果出现异常，异常原因为：", ex); }
		finally
		{ IOUtil.closeQuietly(dataOutputStream, inputStream, inputStreamReader, bufferedReader); }
		
		return Optional.ofNullable(result);
	}
}