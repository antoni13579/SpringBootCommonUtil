package com.CommonUtils.Utils.RemoteUtils;

import java.io.Closeable;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CollectionUtils.CustomCollections.Properties;
import com.CommonUtils.Utils.IOUtils.IOUtil;
import com.CommonUtils.Utils.RemoteUtils.Bean.RemoteInfo;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.Getter;

public final class RemoteUtil 
{
	@Getter(lazy = true)
	private static final JSch JSCH = new JSch();
	
	private RemoteUtil() {}
	
	public static void releaseResource(final Channel[] channels, final Session[] sessions, final Closeable ... closeables)
	{
		IOUtil.closeQuietly(closeables);
		
		ArrayUtil.arrayProcessor
		(
				channels, 
				(channel, index, length) -> { if (null != channel) { channel.disconnect(); } }
		);
		
		ArrayUtil.arrayProcessor
		(
				sessions,
				(session, indx, length) -> { if (null != session) { session.disconnect(); } }
		);
	}
	
	/**
	 * 判断Channel是否处于连接状态，true为已连接，false为未连接
	 * */
	public static boolean isConnected(final Channel channel)
	{ return null != channel && channel.isConnected(); }
	
	/**
	 * 判断Session是否处于连接状态，true为已连接，false为未连接
	 * */
	public static boolean isConnected(final Session session)
	{ return null != session && session.isConnected(); }
	
	public static Session getSession(final RemoteInfo remoteInfo) throws JSchException
	{        
        Session session = getJSCH().getSession(remoteInfo.getUsername(), remoteInfo.getHost(), remoteInfo.getPort());
        session.setPassword(remoteInfo.getPassword());
        session.setConfig(new Properties().setProperty("StrictHostKeyChecking", "no").getProperties());
        session.connect();
        return session;
	}
	
	public static ChannelExec getChannelExec(final Session session, final String command) throws JSchException
	{
		Channel channel = session.openChannel("exec");
		ChannelExec channelExec = (ChannelExec)channel;
		channelExec.setCommand(command);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        channelExec.connect();
		return channelExec;
	}
	
	public static ChannelSftp getChannelSftp(final Session session) throws JSchException
	{
		Channel channel = session.openChannel("sftp");
		ChannelSftp channelSftp = (ChannelSftp)channel;
		channelSftp.connect();
		return channelSftp;
	}
}