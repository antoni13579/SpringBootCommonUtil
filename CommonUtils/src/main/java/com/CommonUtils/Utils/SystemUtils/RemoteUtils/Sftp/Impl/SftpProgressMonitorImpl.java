package com.CommonUtils.Utils.SystemUtils.RemoteUtils.Sftp.Impl;

import com.jcraft.jsch.SftpProgressMonitor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SftpProgressMonitorImpl implements SftpProgressMonitor
{
	private long count = 0;     //当前接收的总字节数
    private long max = 0;       //最终文件大小
    private long percent = -1;  //进度
    private String msg;
    private String src;
    private String dest;
    
	@Override
	public void init(int op, String src, String dest, long max) 
	{
		this.max = max;
        this.count = 0;
        this.percent = -1;
        this.src = src;
        this.dest = dest;
        
        if (op == SftpProgressMonitor.GET)
		{ this.msg = "下载"; }
		else if (op == SftpProgressMonitor.PUT)
		{ this.msg = "上传"; }
		else
		{ log.warn("SFTP操作出现新的类型，请及时处理"); }
	}

	@Override
	public boolean count(long count) 
	{
		this.count += count;
		if (this.percent >= this.count / this.max * 100) 
		{ return true; }
		
		this.percent = this.count / this.max * 100;
		log.info("总大小为{}，当前{}了{}，进度为{}%，源路径为{}，目录路径为{}", this.max, this.msg, this.count, this.percent, this.src, this.dest);
		
		return false;
	}

	@Override
	public void end() 
	{ log.debug("{}结束", this.msg); }
}