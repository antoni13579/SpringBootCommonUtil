package com.CommonUtils.Utils.ExecuteCommandUtils;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;
import com.CommonUtils.Utils.SystemUtils.SystemInfoUtils.SystemInfo;
import com.CommonUtils.Utils.SystemUtils.SystemInfoUtils.SystemInfo.SystemPlatform;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
/**
 * Windows命令
 * cmd /c dir 是执行完dir命令后关闭命令窗口。
   cmd /k dir 是执行完dir命令后不关闭命令窗口。
   cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
   cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
   echo 当前盘符：%~d0
   echo 当前盘符和路径：%~dp0
   echo 当前批处理全路径：%~f0
   echo 当前盘符和路径的短文件名格式：%~sdp0
   echo 当前CMD默认目录：%cd%
   echo 目录中有空格也可以加入""避免找不到路径
   echo 当前盘符和路径的短文件名格式："%~sdp0"
   echo 当前CMD默认目录："%cd%"
 * 
 * 
 * 
 * */
public final class ExecuteCommand 
{
	private String command;
	private String encode;
	
	public ExecuteCommand() {}
	
	public ExecuteCommand(final String command, final String encode)
	{
		this.command = command;
		this.encode = encode;
	}
	
	public ExecuteCommand(final String command)
	{ this.command = command; }
	
	public void executeCommand()
	{
		String cmd;
		if (SystemInfo.isWindows())
		{ cmd = "cmd /c "; }
		else
		{
			log.warn("出现了新的操作系统类型，无法判断如何处理，直接退出");
			return;
		}
		
		execute(cmd);
	}
	
	public void executeCommand(final SystemPlatform systemPlatform)
	{
		String cmd = null;
		switch (systemPlatform)
		{
			case Windows:
				cmd = "cmd /c ";
				break;
			default:
				log.warn("出现了新的操作系统类型，无法判断如何处理，直接退出");
				return;
		}
		
		execute(cmd);
	}
	
	private void execute(final String cmd)
	{
		if (StringUtil.isStrEmpty(this.encode))
		{ this.encode = "gbk"; }
		
		try
		{
			Process process = Runtime.getRuntime().exec(cmd + this.command);
			StreamListener outputStreamListener = new StreamListener(process.getInputStream(), "Output", this.command, this.encode);
			StreamListener errorStreamListener = new StreamListener(process.getErrorStream(), "Error", this.command, this.encode);
			
			errorStreamListener.start();
			outputStreamListener.start();
			process.waitFor();
		}
		catch (Exception ex)
		{ log.error("执行命令，命令为{}，出现了异常，异常原因为：", this.command, ex); }
	}
}