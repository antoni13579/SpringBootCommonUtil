package com.CommonUtils.Utils.CompressAndDecompressUtils.SubSystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

/**已废弃，请使用cn.hutool.core.util.ZipUtil
 * @deprecated
 * */
@Deprecated(since="已废弃，请使用cn.hutool.core.util.ZipUtil")
@Slf4j
public final class ZipUtil 
{
	private ZipUtil() {}
	
	private static void writeFile(final File sourceFile, 
			 					  final ZipArchiveOutputStream zaos, 
			 					  //FileInputStream fis,
			 					  //BufferedInputStream bis,
			 					  final String targetName) throws IOException
	{
		try
		(
				FileInputStream fis1 = new FileInputStream(sourceFile);
				BufferedInputStream bis1 = new BufferedInputStream(fis1);	
		)
		{
			zaos.putArchiveEntry(new ZipArchiveEntry(targetName));
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = bis1.read(buffer)) != -1)
			{ zaos.write(buffer, 0, len); }
			zaos.flush();
		}
		catch (Exception ex)
		{ throw new IOException(ex); }
		finally
		{ zaos.closeArchiveEntry();	}
	}
		
	private static void writeDirectory(final ZipArchiveOutputStream zaos, 
			  						   final String targetName) throws IOException
	{
		zaos.putArchiveEntry(new ZipArchiveEntry(targetName));
		zaos.closeArchiveEntry();
	}
	
	private static void compress(final File sourceFile, 
								 final String destName,
								 final ZipArchiveOutputStream zaos,
								 FileInputStream fis,
			 					 BufferedInputStream bis) throws IOException
	{
		//类型为目录
		if (sourceFile.isDirectory())
		{
			writeDirectory(zaos, destName + File.separator);
			File[] files = sourceFile.listFiles();
			Optional<File[]> tmpFiles = Optional.ofNullable(files);
			if (tmpFiles.isPresent())
			{
				for (File file : files)
				{
					File tmp = Optional.ofNullable(file).orElseThrow();
					String name = destName + File.separator + tmp.getName();
					compress(tmp, name, zaos, fis, bis);
				}
			}
		}
		//类型为文件
		else
		{ writeFile(sourceFile, zaos, destName); }
	}
	
	public static void compress(final Path zipPath, final Path ... sourcePaths)
	{
		if (ArrayUtil.isArrayEmpty(sourcePaths))
		{ return; }
		
		Collection<File> sourceFiles = new ArrayList<>();
		for (Path sourcePath : sourcePaths)
		{ sourceFiles.add(sourcePath.toFile()); }
		
		compress(zipPath.toFile(), sourceFiles.toArray(new File[sourceFiles.size()]));
	}
	
	public static void compress(final String zipFilePath, final String ... sourceFilePaths)
	{
		if (ArrayUtil.isArrayEmpty(sourceFilePaths))
		{ return; }
		
		Collection<File> sourceFiles = new ArrayList<>();
		for (String sourceFilePath : sourceFilePaths)
		{ sourceFiles.add(new File(sourceFilePath)); }
		
		compress(new File(zipFilePath), sourceFiles.toArray(new File[sourceFiles.size()]));
	}
	
	public static void compress(final File zipFile, final File ... sourceFiles)
	{
		if (ArrayUtil.isArrayEmpty(sourceFiles))
		{ return; }
		
		try
		(
				ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(zipFile);
				FileInputStream fis = null;
				BufferedInputStream bis = null;
		)
		{
			
			zaos.setUseZip64(Zip64Mode.AsNeeded);
			for (File sourceFile : sourceFiles)
			{ compress(sourceFile, sourceFile.getName(), zaos, fis, bis); }
			zaos.finish();
		}
		catch (Exception ex)
		{ log.error("压缩为zip文件出现异常，异常原因为：", ex); }
	}
	
	public static void decompress(final Path srcPath, final Path saveFileDirectoryPath, final String charsetName)
	{ decompress(srcPath.toFile(), saveFileDirectoryPath.toFile(), charsetName); }
	
	public static void decompress(final String srcFilePath, final String saveFileDirectoryPath, final String charsetName)
	{ decompress(new File(srcFilePath), new File(saveFileDirectoryPath), charsetName); }
	
	public static void decompress(final File srcFile, final File saveFileDirectory, final String charsetName)
	{
		ZipFile zipFile = null;
		try
		{
			if (!(saveFileDirectory.mkdir()))
			{ throw new ZipUtilException("解压zip文件失败，因无法创建目录，目录名称为：" + saveFileDirectory.getAbsolutePath()); }
			
			zipFile = new ZipFile(srcFile, charsetName);
			Enumeration<ZipArchiveEntry> files = zipFile.getEntries();
			while (files.hasMoreElements())
			{
				ZipArchiveEntry zipArchiveEntry = files.nextElement();
				String entryFileName = zipArchiveEntry.getName();
				String entryFilePath = saveFileDirectory.getAbsolutePath() + File.separator + entryFileName;
				if (zipArchiveEntry.isDirectory())
				{
					if (!(new File(entryFilePath).mkdir()))
					{
						zipFile.close();
						throw new ZipUtilException("解压zip文件失败，因无法创建目录，目录名称为：" + saveFileDirectory.getAbsolutePath());
					}
				}
				else
				{		            
		            try
		            (
		            		InputStream is = zipFile.getInputStream(zipArchiveEntry);
		            		BufferedInputStream bis = IoUtil.toBuffered(is);
		            		FileOutputStream fos = new FileOutputStream(new File(entryFilePath));
		            		BufferedOutputStream bos = IoUtil.toBuffered(fos);
		            )
		            { IoUtil.copy(bis, bos); }
		            catch(IOException e)
		            { throw new IOException(e); }
				}
			}
		}
		catch (Exception ex)
		{ log.error("解压缩Zip文件异常，异常原因为：", ex); }
		finally
		{ ZipFile.closeQuietly(zipFile); }
	}
	
	public static boolean isEndsWithZip(final String fileName)
	{
		boolean result = false;
		if (!StringUtil.isStrEmpty(fileName) && (fileName.endsWith(".ZIP") || fileName.endsWith(".zip")))
		{ result = true; }
		return result;
	}
	
	public static boolean isEndsWithZip(final File file)
	{ return isEndsWithZip(file.getAbsolutePath()); }
	
	public static boolean isEndsWithZip(final Path path)
	{
		boolean result = false;
		if (null != path) { result = isEndsWithZip(path.toFile()); }
		return result;
	}
	
	private static class ZipUtilException extends Exception
	{
		private static final long serialVersionUID = 1334640323108169097L;

		private ZipUtilException(final String message)
		{ super(message); }
	}
}