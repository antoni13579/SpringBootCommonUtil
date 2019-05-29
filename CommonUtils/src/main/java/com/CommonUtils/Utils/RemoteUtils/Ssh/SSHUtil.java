package com.CommonUtils.Utils.RemoteUtils.Ssh;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.RemoteUtils.RemoteUtil;
import com.CommonUtils.Utils.RemoteUtils.Bean.RemoteInfo;
import com.CommonUtils.Utils.StringUtils.StringUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SSHUtil 
{	
	private SSHUtil() {}
	
	public synchronized static List<String> execute(final String command, final RemoteInfo sshInfo)
	{
		if (StringUtil.isStrEmpty(command) || (null == sshInfo))
		{ return Collections.emptyList(); }
		
		Session session = null;
		ChannelExec channelExec = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		List<String> result = null;
		
		try
		{
			session = RemoteUtil.getSession(sshInfo);
			channelExec = RemoteUtil.getChannelExec(session, command);
			
            is = channelExec.getInputStream();
            isr = new InputStreamReader(is, sshInfo.getEncode());
            br = new BufferedReader(isr);
            
            String stdoutLine;
            List<String> tmpList = new ArrayList<String>();
            while (null != (stdoutLine = br.readLine())) 
            { tmpList.add(stdoutLine); }
            
            if (JavaCollectionsUtil.isCollectionEmpty(tmpList)) { result = Collections.emptyList(); }
            else { result = tmpList; }
		}
		catch (Exception ex)
		{ log.error("执行系统命令出现异常，主机为{}，执行命令为{}，异常原因为：", sshInfo.getHost(), command, ex); }
		finally
		{ RemoteUtil.releaseResource(new ChannelExec[] { channelExec }, new Session[] { session }, is, isr, br); }
		
		return result;
	}
}