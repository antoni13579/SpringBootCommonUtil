package com.CommonUtils.Utils.CompressAndDecompressUtils.Facade;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.CompressAndDecompressUtils.ECompressAndDecompress;
import com.CommonUtils.Utils.CompressAndDecompressUtils.SubSystem.ZipUtil;
import com.CommonUtils.Utils.IOUtils.FileUtil;

public final class CompressAndDecompressUtil 
{
	private CompressAndDecompressUtil() {}
	
	public static void compress(final ECompressAndDecompress eCompressAndDecompress, final Path zipPath, final Path ... sourcePaths) throws Exception
	{
		if (ArrayUtil.isArrayEmpty(sourcePaths))
		{ return; }
		
		Collection<File> sourceFiles = new ArrayList<>();
		for (Path sourcePath : sourcePaths)
		{ sourceFiles.add(sourcePath.toFile()); }
		
		compress(eCompressAndDecompress, zipPath.toFile(), sourceFiles.toArray(new File[sourceFiles.size()]));
	}
	
	public static void compress(final ECompressAndDecompress eCompressAndDecompress, final String zipFilePath, final String ... sourceFilePaths) throws Exception
	{
		if (ArrayUtil.isArrayEmpty(sourceFilePaths))
		{ return; }
		
		Collection<File> sourceFiles = new ArrayList<>();
		for (String sourceFilePath : sourceFilePaths)
		{ sourceFiles.add(new File(sourceFilePath)); }
		
		compress(eCompressAndDecompress, new File(zipFilePath), sourceFiles.toArray(new File[sourceFiles.size()]));
	}
	
	public static void compress(final ECompressAndDecompress eCompressAndDecompress, final File compressFile, final File ... sourceFiles) throws Exception
	{
		switch (eCompressAndDecompress)
		{
			case ZIP:
				ZipUtil.compress(compressFile, sourceFiles);
				break;
			default:
				throw new Exception("需要压缩的文件类型不正确，无法处理");
		}
	}
	
	public static void decompress(final Path srcPath, final Path saveDirectoryPath, final String charsetName) throws Exception
	{ decompress(srcPath.toFile(), saveDirectoryPath.toFile(), charsetName); }
	
	public static void decompress(final String srcFilePath, final String saveFileDirectoryPath, final String charsetName) throws Exception
	{ decompress(new File(srcFilePath), new File(saveFileDirectoryPath), charsetName); }
	
	public static void decompress(final File srcFile, final File saveFileDirectory, final String charsetName) throws Exception
	{
		if (!FileUtil.isFile(srcFile))
		{ throw new Exception("源文件有问题，可能是对象为空、文件不存在或不是文件类型"); }
		
		Collection<String> fileTypes = FileUtil.getFileType(srcFile);
		if (!JavaCollectionsUtil.isCollectionEmpty(fileTypes))
		{
			for (String fileType : fileTypes)
			{
				ECompressAndDecompress[] eCompressAndDecompresses = ECompressAndDecompress.values();
				for (ECompressAndDecompress eCompressAndDecompress : eCompressAndDecompresses)
				{
					if (eCompressAndDecompress.name().equalsIgnoreCase(fileType))
					{
						switch (eCompressAndDecompress)
						{
							case ZIP:
								ZipUtil.decompress(srcFile, saveFileDirectory, charsetName);
								break;
							default:
								throw new Exception("需要解压缩压缩的文件类型不正确，无法处理");
						}
					}
				}
			}
		}
		else
		{ throw new Exception("无法获取其文件类型，无法进行后续处理"); }
	}
}