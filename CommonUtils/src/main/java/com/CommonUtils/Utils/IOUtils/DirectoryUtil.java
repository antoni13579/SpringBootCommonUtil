package com.CommonUtils.Utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public final class DirectoryUtil 
{
	/**
	 * 判断指定的路径是否为目录，目录返回true，不是则返回false，建议使用cn.hutool.core.io.FileUtil.isDirectory
	 * */
	public static boolean isDirectory(final String path)
	{
		File file = new File(path);
		if (file.exists() && file.isDirectory() && !StringUtil.isStrEmpty(path))
		{ return true; }
		
		return false;
	}
	
	/**
	 * 判断指定的路径是否为目录，目录返回true，不是则返回false，建议使用cn.hutool.core.io.FileUtil.isDirectory
	 * */
	public static boolean isDirectory(final File file)
	{
		if (null != file && file.exists() && file.isDirectory())
		{ return true; }
		
		return false;
	}
	
	/**
	 * 判断指定的路径是否为目录，目录返回true，不是则返回false，建议使用cn.hutool.core.io.FileUtil.isDirectory
	 * */
	public static boolean isDirectory(final Path path)
	{
		if (null == path)
		{ return false; }
		else
		{ return isDirectory(path.toFile()); }
	}
	
	/**
	 * 根据文件路径，获取其目录地址，建议使用cn.hutool.core.io.FileUtil.getParent
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
	
	/**
	 * 根据文件路径，获取其目录地址，建议使用cn.hutool.core.io.FileUtil.getParent
	 * */
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
	
	/**
	 * 根据文件路径，获取其目录地址，建议使用cn.hutool.core.io.FileUtil.getParent
	 * */
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
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	private static void subCopyDirectoryNio(final File srcDirectoryFile, final File destDirectoryFile) throws FileNotFoundException
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
            //String result = FileUtil.copyFileNio(srcDirectoryFile.getPath(), destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName());
            
			FileInputStream fis = new FileInputStream(new File(srcDirectoryFile.getPath()));
			FileOutputStream fos = new FileOutputStream(new File(destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName()));
            IoUtil.copy(fis, fos);
            IoUtil.close(fis);
            IoUtil.close(fos);
            
            /*
            if ("FAILED".equalsIgnoreCase(result))
            {
            	log.warn("采用Nio目录复制出现了异常，请查询日志，程序退出，源路径={}，目标路径={}", srcDirectoryFile.getPath(), destDirectoryFile.getPath());
            	return;
            }
            */
        }
	}
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	public static void copyDirectoryNio(final File srcDirectoryFile, final File destDirectoryFile) throws FileNotFoundException
	{
		long startTime = System.currentTimeMillis();
		subCopyDirectoryNio(srcDirectoryFile, destDirectoryFile);
		double seconds = (System.currentTimeMillis() - startTime) / 1000D;
		log.info("采用Nio复制目录，源路径={}，目标路径={}，耗时{}秒", srcDirectoryFile.getPath(), destDirectoryFile.getPath(), seconds);
	}
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	public static void copyDirectoryNio(final String srcDirectoryFilePath, final String destDirectoryFilePath) throws FileNotFoundException
	{ copyDirectoryNio(new File(srcDirectoryFilePath), new File(destDirectoryFilePath)); }
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	public static void copyDirectoryNio(final Path srcDirectoryPath, final Path destDirectoryPath) throws FileNotFoundException
	{ copyDirectoryNio(srcDirectoryPath.toFile(), destDirectoryPath.toFile()); }
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	private static void subCopyDirectoryBio(final File srcDirectoryFile, final File destDirectoryFile) throws FileNotFoundException
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
            //String result = FileUtil.copyFileBio(srcDirectoryFile.getPath(), destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName());
            
            FileInputStream fis = new FileInputStream(new File(srcDirectoryFile.getPath()));
            BufferedInputStream bis = IoUtil.toBuffered(fis);
            
            FileOutputStream fos = new FileOutputStream(new File(destDirectoryFile.getAbsolutePath() + File.separator + srcDirectoryFile.getName()));
            BufferedOutputStream bos = IoUtil.toBuffered(fos);
            
            IoUtil.copy(bis, bos);
            
            IoUtil.close(bis);
            IoUtil.close(fis);
            IoUtil.close(bos);
            IoUtil.close(fos);
            /*
            if ("FAILED".equalsIgnoreCase(result))
            {
            	log.warn("采用Bio目录复制出现了异常，请查询日志，程序退出，源路径={}，目标路径={}", srcDirectoryFile.getPath(), destDirectoryFile.getPath());
            	return;
            }
            */
        }
	}
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	public static void copyDirectoryBio(final File srcDirectoryFile, final File destDirectoryFile) throws FileNotFoundException
	{
		long startTime = System.currentTimeMillis();
		subCopyDirectoryBio(srcDirectoryFile, destDirectoryFile);
		double seconds = (System.currentTimeMillis() - startTime) / 1000D;
		log.info("采用Bio复制目录，源路径={}，目标路径={}，耗时{}秒", srcDirectoryFile.getPath(), destDirectoryFile.getPath(), seconds);
	}
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	public static void copyDirectoryBio(final String srcDirectoryFilePath, final String destDirectoryFilePath) throws FileNotFoundException
	{ copyDirectoryBio(new File(srcDirectoryFilePath), new File(destDirectoryFilePath)); }
	
	/**建议使用cn.hutool.core.io.FileUtil.copy相关方法*/
	public static void copyDirectoryBio(final Path srcDirectoryPath, final Path destDirectoryPath) throws FileNotFoundException
	{ copyDirectoryBio(srcDirectoryPath.toFile(), destDirectoryPath.toFile()); }
}