package com.CommonUtils.Utils.RemoteUtils.Sftp;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.IOUtils.DirectoryUtil;
import com.CommonUtils.Utils.IOUtils.FileUtil;
import com.CommonUtils.Utils.IOUtils.IOUtil;
import com.CommonUtils.Utils.RemoteUtils.RemoteUtil;
import com.CommonUtils.Utils.RemoteUtils.Bean.RemoteInfo;
import com.CommonUtils.Utils.RemoteUtils.Sftp.Impl.SftpProgressMonitorImpl;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SFtpUtil 
{
	private SFtpUtil() {}
	
	private synchronized static boolean execute(final RemoteInfo remoteInfo, final Processor ... processors)
	{
		Session session = null;
		ChannelSftp channel = null;
		InputStream is = null;
		boolean result = false;
		try
		{
			session = RemoteUtil.getSession(remoteInfo);
			channel = RemoteUtil.getChannelSftp(session);
			
			if (!ArrayUtil.isArrayEmpty(processors))
			{
				for (Processor processor : processors)
				{ processor.process(channel, is); }
			}
			
			result = true;
		}
		catch (Exception ex)
		{
			log.error("SFTP操作出现异常，主机地址为{}，异常原因为：", remoteInfo.getHost(), ex);
			result = false;
		}
		finally
		{ RemoteUtil.releaseResource(new ChannelSftp[] { channel }, new Session[] { session }, is); }
		
		return result;
	}
	
	public synchronized static boolean downloadFile(final File localDirectory, final String remoteDirectory, final RemoteInfo remoteInfo)
	{
		if (DirectoryUtil.isDirectory(localDirectory))
		{
			File[] localFiles = localDirectory.listFiles();
			if (!ArrayUtil.isArrayEmpty(localFiles))
			{
				Set<Boolean> executeResult = new HashSet<>();
				ArrayUtil.arrayProcessor
				(
						localFiles, 
						(localFile, indx) -> 
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
	
	public synchronized static boolean downloadFile(final String localFilePath, final String remoteFilePath, final RemoteInfo remoteInfo)
	{
		return execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> 
				{ channel.get(remoteFilePath, localFilePath, new SftpProgressMonitorImpl(), ChannelSftp.RESUME); }
		);
	}
	
	public synchronized static boolean uploadFile(final RemoteInfo remoteInfo, final String remoteFileDirectory, final MultipartFile ... localFileMultipartFiles)
	{
		if (!ArrayUtil.isArrayEmpty(localFileMultipartFiles))
		{
			Set<Boolean> executeResult = new HashSet<>();
			
			ArrayUtil.arrayProcessor
			(
					localFileMultipartFiles, 
					(localFileMultipartFile, indx) -> 
					{
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
											IOUtil.closeQuietly(is);
											is = null;
										}
								)
						);
					}
			);
			
			return JavaCollectionsUtil.getOperationFlowResult(executeResult);
		}
		else
		{ return false; }
	}
	
	public synchronized static boolean uploadFile(final RemoteInfo remoteInfo, final String localFilePath, final String remoteFilePath)
	{
		return execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> 
				{
					int mode = FileUtil.getFileSize(localFilePath) > 0 ? ChannelSftp.RESUME : ChannelSftp.OVERWRITE;
					channel.put(localFilePath, remoteFilePath, new SftpProgressMonitorImpl(), mode);
				}
		);
	}
	
	public synchronized static boolean uploadFile(final RemoteInfo remoteInfo, final File localDirectory, final String remoteDirectory)
	{
		if (DirectoryUtil.isDirectory(localDirectory))
		{
			File[] localFiles = localDirectory.listFiles();
			if (!ArrayUtil.isArrayEmpty(localFiles))
			{
				Set<Boolean> executeResult = new HashSet<>();
				ArrayUtil.arrayProcessor
				(
						localFiles, 
						(localFile, indx) -> 
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
	
	public synchronized static Collection<ChannelSftp.LsEntry> getFiles(final RemoteInfo remoteInfo, final String remotePath)
	{
		Collection<ChannelSftp.LsEntry> result = new HashSet<>();
		execute
		(
				remoteInfo, 
				(ChannelSftp channel, InputStream is) -> 
				{ result.addAll(CommonUtil.cast(channel.ls(remotePath))); }
		);
		return result;
	}
	
	public synchronized static void deleteFile(final RemoteInfo remoteInfo, final String directory, final String fileName)
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