package com.CommonUtils.Utils.SystemUtils.RemoteUtils.Sftp;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.SystemUtils.RemoteUtils.Bean.RemoteInfo;
import com.CommonUtils.Utils.SystemUtils.RemoteUtils.Sftp.Impl.SftpProgressMonitorImpl;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.ssh.JschUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * hutool已封装了对应的工具类
 * @deprecated
 * */
@Deprecated(since="hutool已封装了对应的工具类")
@Slf4j
public final class SFtpUtil 
{
	private SFtpUtil() {}
	
	private static synchronized boolean execute(final RemoteInfo remoteInfo, final Processor ... processors)
	{
		Session session = null;
		ChannelSftp channel = null;
		InputStream is = null;
		boolean result = false;
		try
		{
			session = JschUtil.getSession(remoteInfo.getHost(), remoteInfo.getPort(), remoteInfo.getUsername(), remoteInfo.getPassword());
			channel = JschUtil.openSftp(session);
			
			if (!cn.hutool.core.util.ArrayUtil.isEmpty(processors))
			{
				for (Processor processor : processors)
				{ processor.process(channel, is); }
			}
			
			result = true;
		}
		catch (Exception ex)
		{ log.error("SFTP操作出现异常，主机地址为{}，异常原因为：", remoteInfo.getHost(), ex); }
		finally
		{
			JschUtil.close(channel);
			JschUtil.close(session);
			JschUtil.closeAll();
			IoUtil.close(is);
		}
		
		return result;
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.download或get相关方法*/
	public static synchronized boolean downloadFile(final File localDirectory, final String remoteDirectory, final RemoteInfo remoteInfo)
	{
		if (FileUtil.isDirectory(localDirectory))
		{
			File[] localFiles = localDirectory.listFiles();
			if (!cn.hutool.core.util.ArrayUtil.isEmpty(localFiles))
			{
				Set<Boolean> executeResult = new HashSet<>();
				ArrayUtil.arrayProcessor
				(
						localFiles, 
						(localFile, indx, length) -> 
						{
							String localFilePath = localFile.getAbsolutePath();
							String remoteFilePath = remoteDirectory + localFile.getName();
							executeResult.add(downloadFile(localFilePath, remoteFilePath, remoteInfo));
						}
				);
				return JavaCollectionsUtil.getOperationFlowResult(executeResult);
			}
			else
			{ return false; }
		}
		else
		{ return false; }
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.download或get相关方法*/
	public static synchronized boolean downloadFile(final String localFilePath, final String remoteFilePath, final RemoteInfo remoteInfo)
	{
		return execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> channel.get(remoteFilePath, localFilePath, new SftpProgressMonitorImpl(), ChannelSftp.RESUME)
		);
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.upload或put相关方法*/
	public static synchronized boolean uploadFile(final RemoteInfo remoteInfo, final String remoteFileDirectory, final MultipartFile ... localFileMultipartFiles)
	{
		if (!cn.hutool.core.util.ArrayUtil.isEmpty(localFileMultipartFiles))
		{
			Set<Boolean> executeResult = new HashSet<>();
			
			ArrayUtil.arrayProcessor
			(
					localFileMultipartFiles, 
					(localFileMultipartFile, indx, length) -> 
						executeResult.add
						(
								execute
								(
										remoteInfo,
										(final ChannelSftp channel, InputStream is) -> 
										{
											String remoteFilePath = remoteFileDirectory + localFileMultipartFile.getOriginalFilename();
											int mode = localFileMultipartFile.getSize() > 0 ? ChannelSftp.RESUME : ChannelSftp.OVERWRITE;
											is = localFileMultipartFile.getInputStream();
											channel.put(is, remoteFilePath, new SftpProgressMonitorImpl(), mode);
											IoUtil.close(is);
											is = null;
										}
								)
						)
			);
			
			return JavaCollectionsUtil.getOperationFlowResult(executeResult);
		}
		else
		{ return false; }
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.upload或put相关方法*/
	public static synchronized boolean uploadFile(final RemoteInfo remoteInfo, final String localFilePath, final String remoteFilePath)
	{
		return execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> 
				{
					int mode = FileUtil.size(new File(localFilePath)) > 0 ? ChannelSftp.RESUME : ChannelSftp.OVERWRITE;
					channel.put(localFilePath, remoteFilePath, new SftpProgressMonitorImpl(), mode);
				}
		);
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.upload或put相关方法*/
	public static synchronized boolean uploadFile(final RemoteInfo remoteInfo, final File localDirectory, final String remoteDirectory)
	{
		if (FileUtil.isDirectory(localDirectory))
		{
			File[] localFiles = localDirectory.listFiles();
			if (!cn.hutool.core.util.ArrayUtil.isEmpty(localFiles))
			{
				Set<Boolean> executeResult = new HashSet<>();
				ArrayUtil.arrayProcessor
				(
						localFiles, 
						(localFile, indx, length) -> 
						{
							String localFilePath = localFile.getAbsolutePath();
							String remoteFilePath = remoteDirectory + localFile.getName();
							executeResult.add(uploadFile(remoteInfo, localFilePath, remoteFilePath));
						}
				);
				return JavaCollectionsUtil.getOperationFlowResult(executeResult);
			}
			else
			{ return false; }
		}
		else
		{ return false; }
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.ls相关方法*/
	public static synchronized Collection<ChannelSftp.LsEntry> getFiles(final RemoteInfo remoteInfo, final String remotePath)
	{
		Collection<ChannelSftp.LsEntry> result = new HashSet<>();
		
		execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> result.addAll(Convert.convert(new TypeReference<Collection<? extends ChannelSftp.LsEntry> >() {}, channel.ls(remotePath)))
		);
		return result;
	}
	
	/**请使用cn.hutool.extra.ssh.Sftp.del相关方法*/
	public static synchronized void deleteFile(final RemoteInfo remoteInfo, final String directory, final String fileName)
	{
		execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> 
				{
					channel.cd(directory);
					channel.rm(directory + fileName);
				}
		);
	}
	
	@FunctionalInterface
	public interface Processor
	{ void process(final ChannelSftp channel, InputStream is) throws Exception; }
}