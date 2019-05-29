package com.CommonUtils.Utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.BytesUtils.BytesUtil;
import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.StringUtils.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FileUtil 
{
	/**
	 * 判断指定的路径是否为文件类型，文件类型返回true，不是则返回false
	 * */
	public static boolean isFile(final String path)
	{
		File file = new File(path);
		if (file.exists() && file.isFile() && !StringUtil.isStrEmpty(path))
		{ return true; }
		
		return false;
	}
	
	/**
	 * 判断指定的路径是否为文件类型，文件类型返回true，不是则返回false
	 * */
	public static boolean isFile(final File file)
	{
		if (null != file && file.exists() && file.isFile())
		{ return true; }
		
		return false;
	}
	
	public static boolean isFile(final Path path)
	{
		if (null == path)
		{ return false; }
		else
		{ return isFile(path.toFile()); }
	}
	
	public static boolean writeInfo(final File file, final boolean append, final String encode, final String info)
	{
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		boolean result = false;
		try
		{
			fos = new FileOutputStream(file, append);
			osw = new OutputStreamWriter(fos, encode);
			bw = new BufferedWriter(osw);
			bw.write(info);
			bw.flush();
			result = true;
		}
		catch (Exception ex)
		{
			log.error("写入数据到文件异常，异常原因为：", ex);
			result = false;
		}
		finally
		{ IOUtil.closeQuietly(fos, osw, bw); }
		
		return result;
	}
	
	/**
	 * 创建文件（仅仅没有的时候才创建），并返回对应的File对象列表
	 * */
	public static Collection<File> createFiles(final File ... files)
	{
		if (!ArrayUtil.isArrayEmpty(files))
		{
			List<File> result = new ArrayList<File>();
			ArrayUtil.arrayProcessor
			(
					files, 
					(final File file, final int indx) -> 
					{
						try
						{
							if (!Files.exists(file.toPath()))
							{ file.createNewFile(); }
							
							result.add(file);
						}
						catch (Exception ex)
						{ log.error("创建文件异常，异常原因为：", ex); }
					}
			);
			return result;
		}
		else
		{ return Collections.emptyList(); }
	}
	
	public static Collection<File> createFiles(final String ... filePaths)
	{
		if (!ArrayUtil.isArrayEmpty(filePaths))
		{
			List<File> files = new ArrayList<>();
			ArrayUtil.arrayProcessor
			(
					filePaths, 
					(final String filePath, final int indx) -> 
					{ files.add(new File(filePath)); }
			);
			
			return createFiles(files.toArray(new File[filePaths.length]));
		}
		else
		{ return Collections.emptyList(); }
	}
	
	public static Collection<File> createFiles(final Path ... paths)
	{
		if (!ArrayUtil.isArrayEmpty(paths))
		{
			List<File> files = new ArrayList<>();
			ArrayUtil.arrayProcessor
			(
					paths, 
					(final Path path, final int indx) -> 
					{ files.add(path.toFile()); }
			);
			
			return createFiles(files.toArray(new File[paths.length]));
		}
		else
		{ return Collections.emptyList(); }
	}
	
	/**
	 * 采用Nio复制文件
	 * */
	public static String copyFileNio(final File srcFile, final File destFile)
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel readChannel = null;
		FileChannel writeChannel = null;
		try
		{
			long startTime = System.currentTimeMillis();
			fis = new FileInputStream(srcFile);
			readChannel = fis.getChannel();
			
			fos = new FileOutputStream(destFile);
			writeChannel = fos.getChannel();
			
			/*
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			
			while (readChannel.read(byteBuffer) != -1)
			{
				
				byteBuffer.flip();					//缓冲区游标置为0
				
				//写数据
				while (byteBuffer.hasRemaining())
				{ writeChannel.write(byteBuffer); }
				
				byteBuffer.clear();					//清空缓冲区
			}
			*/
			
			readChannel.transferTo(0, readChannel.size(), writeChannel);
			
			writeChannel.force(true);				//强制将内存中数据刷新到硬盘，boolean代表是否刷新属性
			double seconds = (System.currentTimeMillis() - startTime) / 1000D;
			log.info("采用Nio复制文件，源文件={}，目标文件={}，耗时{}秒", srcFile.getPath(), destFile.getPath(), seconds);
			return "SUCCESSED";
		}
		catch (Exception e)
		{ 
			log.error("采用Nio复制文件出现异常，源文件={}，目标文件={}，异常原因为：", srcFile.getPath(), destFile.getPath(), e);
			return "FAILED";
		}
		finally
		{ IOUtil.closeQuietly(fis, fos, readChannel, writeChannel); }
	}
	
	/**
	 * 采用Nio复制文件
	 * */
	public static String copyFileNio(final String srcFilePath, final String destFilePath)
	{ return copyFileNio(new File(srcFilePath), new File(destFilePath)); }
	
	/**
	 * 采用Nio复制文件
	 * */
	public static String copyFileNio(final Path srcPath, final Path destPath)
	{ return copyFileNio(srcPath.toFile(), destPath.toFile()); }
	
	/**
	 * 采用Bio复制文件
	 * */
	public static void copyFileBio(final InputStream srcIs, final Path destPath)
	{ copyFileBio(srcIs, destPath.toFile()); }
	
	/**
	 * 采用Bio复制文件
	 * */
	public static void copyFileBio(final InputStream srcIs, final String destFilePath)
	{ copyFileBio(srcIs, new File(destFilePath)); }
	
	/**
	 * 采用Bio复制文件
	 * */
	public static void copyFileBio(final InputStream srcIs, final File destFile)
	{
		InputStream bis = null;
		OutputStream fos = null;
		OutputStream bos = null;
		try
		{
			bis = new BufferedInputStream(srcIs);
			
			fos = new FileOutputStream(destFile);
			bos = new BufferedOutputStream(fos);
			
			byte[] buffer = new byte[1024];
	        int read = -1;
	        while((read = bis.read(buffer)) != -1)
	        { bos.write(buffer, 0, read); }
	        
	        bos.flush();
		}
		catch (Exception ex)
		{ log.error("采用Bio复制文件出现异常，目标文件={}，异常原因为：", destFile.getPath(), ex); }
		finally
		{ IOUtil.closeQuietly(srcIs, bis, fos, bos); }
	}
	
	/**
	 * 采用Bio复制文件
	 * */
	public static String copyFileBio(final File srcFile, final File destFile)
	{
		InputStream fis = null;
		InputStream bis = null;
		OutputStream fos = null;
		OutputStream bos = null;
		try
		{
			long startTime = System.currentTimeMillis();
			fis = new FileInputStream(srcFile);
			bis = new BufferedInputStream(fis);
			
			fos = new FileOutputStream(destFile);
			bos = new BufferedOutputStream(fos);
			
			byte[] buffer = new byte[1024];
	        int read = -1;
	        while((read = bis.read(buffer)) != -1)
	        { bos.write(buffer, 0, read); }
	        
	        bos.flush();
	        double seconds = (System.currentTimeMillis() - startTime) / 1000D;
			log.info("采用Bio复制文件，源文件={}，目标文件={}，耗时{}秒", srcFile.getPath(), destFile.getPath(), seconds);
			return "SUCCESSED";
		}
		catch (Exception e)
		{
			log.error("采用Bio复制文件出现异常，源文件={}，目标文件={}，异常原因为：", srcFile.getPath(), destFile.getPath(), e);
			return "FAILED";
		}
		finally
		{ IOUtil.closeQuietly(fis, bis, fos, bos); }
	}
	
	/**
	 * 采用Bio复制文件
	 * */
	public static String copyFileBio(final String srcFilePath, final String destFilePath)
	{ return copyFileBio(new File(srcFilePath), new File(destFilePath)); }
	
	/**
	 * 采用Bio复制文件
	 * */
	public static String copyFileBio(final Path srcPath, final Path destPath)
	{ return copyFileBio(srcPath.toFile(), destPath.toFile()); }
	
	public static String getFileHeader(final String filePath, final boolean toUpper)
	{ return getFileHeader(new File(filePath), toUpper); }
	
	public static String getFileHeader(final Path path, final boolean toUpper)
	{ return getFileHeader(path.toFile(), toUpper); }
	
	public static String getFileHeader(final File file, final boolean toUpper)
	{
		InputStream fis = null;
		String result = null;
		
		try
		{
			fis = new FileInputStream(file);
			byte[] b = new byte[100];
			fis.read(b, 0, b.length);
			result = BytesUtil.byteArrayToHex(b);
		}
		catch (Exception ex)
		{ log.error("获取文件头出现异常，异常原因为：{}", ex); }
		finally
		{ IOUtil.closeQuietly(fis); }
		
		return toUpper ? result.toUpperCase() : result;
	}
	
	public static Collection<String> getFileType(final Path path)
	{ return getFileType(path.toFile()); }
	
	public static Collection<String> getFileType(final String filePath)
	{ return getFileType(new File(filePath)); }
	
	public static Collection<String> getFileType(final File file)
	{
		String fileHeader = getFileHeader(file, true);
		Collection<String> result = new HashSet<String>();
		JavaCollectionsUtil.MapProcessor
		(
				IOContants.fileType, 
				(String key, String value, int indx) -> 
				{
					if (fileHeader.startsWith(value))
					{ result.add(key); }
				}
		);
		return result;
	}
	
	public static long getFileTotalRows(final Path path, final String encode)
	{ return getFileTotalRows(path.toFile(), encode); }
	
	public static long getFileTotalRows(final String filePath, final String encode)
	{ return getFileTotalRows(new File(filePath), encode); }
	
	public static long getFileTotalRows(final File file, final String encode)
	{
		InputStream fis = null;
		Reader isr = null;
		BufferedReader br = null;
		
		long result = 0;
		
		try
		{
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, encode);
			br = new BufferedReader(isr);
			
			while (null != br.readLine())
			{ result++; }
		}
		catch (Exception ex)
		{ log.error("获取文件总行数异常，异常原因为：", ex); }
		finally
		{ IOUtil.closeQuietly(fis, isr, br); }
		
		return result;
	}
	
	public static long getFileSize(final Path path)
	{ return getFileSize(path.toFile()); }
	
	public static long getFileSize(final String filePath)
	{ return getFileSize(new File(filePath)); }
	
	public static long getFileSize(final File file)
	{ return file.length(); }
	
	public static boolean uploadFilesLocal(final Path directoryPath, final MultipartFile ... multipartFiles)
	{ return uploadFilesLocal(directoryPath.toFile(), multipartFiles); }
	
	public static boolean uploadFilesLocal(final String directoryStr, final MultipartFile ... multipartFiles)
	{ return uploadFilesLocal(new File(directoryStr), multipartFiles); }
	
	public static boolean uploadFilesLocal(final File directory, final MultipartFile ... multipartFiles)
	{
		if (!ArrayUtil.isArrayEmpty(multipartFiles))
		{
			Set<Boolean> executeRecords = new HashSet<>();
			ArrayUtil.arrayProcessor
			(
					multipartFiles, 
					(multipartFile, indx) -> 
					{
						if (!directory.exists())
						{ directory.mkdir(); }
						
						String filePath = directory.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename();
						try
						{
							multipartFile.transferTo(new File(filePath));
							executeRecords.add(true);
						}
						catch (Exception ex)
						{
							log.error("上传文件异常，需上传的文件名称为{}，异常原因为：", multipartFile.getOriginalFilename(), ex);
							executeRecords.add(false);
						}
					}
			);
			return JavaCollectionsUtil.getOperationFlowResult(executeRecords);
		}
		else
		{ return false; }
	}
}