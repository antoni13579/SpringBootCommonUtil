package com.CommonUtils.Utils.SystemUtils.ExecuteCommandUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.CommonUtils.Utils.IOUtils.IOUtil;

import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public final class StreamListener extends Thread
{
	private InputStream is;
	private String type;
	private String command;
	private String encode;
	
	public StreamListener(final InputStream is, final String type, final String command, final String encode)
	{
		this.is = is;
		this.type = type;
		this.command = command;
		this.encode = encode;
	}
	
	@Override
	public void run()
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		try
		{
			isr = new InputStreamReader(is, this.encode);
			br = new BufferedReader(isr);
			String line = null;
			while (null != (line = br.readLine()))
			{
				if ("error".equalsIgnoreCase(this.type))
				{ log.warn("命令为{}，则标准错误流输出信息：{}", this.command, line); }
				else
				{ log.info("命令为{}，则标准信息流输出信息：{}", this.command, line); }
			}
		}
		catch (Exception ex)
		{
			if ("error".equalsIgnoreCase(this.type))
			{ log.error("监听标准错误流出现了异常，命令为{}，异常原因为：", this.command, ex); }
			else
			{ log.error("监听标准输出流出现了异常，命令为{}，异常原因为：", this.command, ex); }
		}
		finally
		{ IOUtil.closeQuietly(br, isr, this.is); }
	}
}