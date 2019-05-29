package com.CommonUtils.Utils.IOUtils;

import java.io.File;
import java.nio.file.Path;

import com.CommonUtils.Utils.StringUtils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DirectoryUtil 
{
	/**
	 * 判断指定的路径是否为目录，目录返回true，不是则返回false
	 * */
	public static boolean isDirectory(final String path)
	{
		File file = new File(path);
		if (file.exists() && file.isDirectory() && !StringUtil.isStrEmpty(path))
		{ return true; }
		
		return false;
	}
	
	/**
	 * 判断指定的路径是否为目录，目录返回true，不是则返回false
	 * */
	public static boolean isDirectory(final File file)
	{
		if (null != file && file.exists() && file.isDirectory())
		{ return true; }
		
		return false;
	}
	
	public static boolean isDirectory(final Path path)
	{
		if (null == path)
		{ return false; }
		else
		{ return isDirectory(path.toFile()); }
	}
	
	/**
	 * 根据文件路径，获取其目录地址
	 * */
	public static String getDirectory(final String path)
	{
		String result;
		if (FileUtil.isFile(path))
		{
			File file = new File(path);
			result = file.getParentFile().getPath();
		}
		else if (isDirectory(path))
		{ result = path; }
		else
		{ result = ""; }
		
		return result;
	}
	
	public static String getDirectory(final File file)
	{
		String result;
		if (FileUtil.isFile(file))
		{ result = file.getParentFile().getPath(); }
		else if (isDirectory(file))
		{ result = file.getPath(); }
		else
		{ result = ""; }
		
		return result;
	}
	
	public static String getDirectory(final Path path)
	{
		String result;
		if (FileUtil.isFile(path))
		{ result = path.toFile().getParentFile().getPath(); }
		else if (isDirectory(path))
		{ result = path.toFile().getPath(); }
		else
		{ result = ""; }
		
		return result;
	}
	
	private static void subCopyDirectoryNio(final File srcDirectoryFile, final File destDirectoryFile)
	{
		//类型为目录
		if (srcDirectoryFile.isDirectory()) 
		{
			File tmpFile = new File(destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName());
			tmpFile.mkdir();
			File[] files = srcDirectoryFile.listFiles();
			for (File file : files)
			{ subCopyDirectoryNio(file, tmpFile); }
        }
		
		//类型为文件
		else
		{
            String result = FileUtil.copyFileNio(srcDirectoryFile.getPath(), destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName());
            if ("FAILED".equalsIgnoreCase(result))
            {
            	log.warn("采用Nio目录复制出现了异常，请查询日志，程序退出，源路径={}，目标路径={}", srcDirectoryFile.getPath(), destDirectoryFile.getPath());
            	return;
            }
        }
	}
	
	public static void copyDirectoryNio(final File srcDirectoryFile, final File destDirectoryFile)
	{
		long startTime = System.currentTimeMillis();
		subCopyDirectoryNio(srcDirectoryFile, destDirectoryFile);
		double seconds = (System.currentTimeMillis() - startTime) / 1000D;
		log.info("采用Nio复制目录，源路径={}，目标路径={}，耗时{}秒", srcDirectoryFile.getPath(), destDirectoryFile.getPath(), seconds);
	}
	
	public static void copyDirectoryNio(final String srcDirectoryFilePath, final String destDirectoryFilePath)
	{ copyDirectoryNio(new File(srcDirectoryFilePath), new File(destDirectoryFilePath)); }
	
	public static void copyDirectoryNio(final Path srcDirectoryPath, final Path destDirectoryPath)
	{ copyDirectoryNio(srcDirectoryPath.toFile(), destDirectoryPath.toFile()); }
	
	private static void subCopyDirectoryBio(final File srcDirectoryFile, final File destDirectoryFile)
	{
		//类型为目录
		if (srcDirectoryFile.isDirectory()) 
		{
			File tmpFile = new File(destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName());
			tmpFile.mkdir();
			File[] files = srcDirectoryFile.listFiles();
			for (File file : files)
			{ subCopyDirectoryBio(file, tmpFile); }
        }
		
		//类型为文件
		else
		{
            String result = FileUtil.copyFileBio(srcDirectoryFile.getPath(), destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName());
            if ("FAILED".equalsIgnoreCase(result))
            {
            	log.warn("采用Bio目录复制出现了异常，请查询日志，程序退出，源路径={}，目标路径={}", srcDirectoryFile.getPath(), destDirectoryFile.getPath());
            	return;
            }
        }
	}
	
	public static void copyDirectoryBio(final File srcDirectoryFile, final File destDirectoryFile)
	{
		long startTime = System.currentTimeMillis();
		subCopyDirectoryBio(srcDirectoryFile, destDirectoryFile);
		double seconds = (System.currentTimeMillis() - startTime) / 1000D;
		log.info("采用Bio复制目录，源路径={}，目标路径={}，耗时{}秒", srcDirectoryFile.getPath(), destDirectoryFile.getPath(), seconds);
	}
	
	public static void copyDirectoryBio(final String srcDirectoryFilePath, final String destDirectoryFilePath)
	{ copyDirectoryBio(new File(srcDirectoryFilePath), new File(destDirectoryFilePath)); }
	
	public static void copyDirectoryBio(final Path srcDirectoryPath, final Path destDirectoryPath)
	{ copyDirectoryBio(srcDirectoryPath.toFile(), destDirectoryPath.toFile()); }
}