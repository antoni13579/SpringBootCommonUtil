package com.CommonUtils.Utils.CompressAndDecompressUtils.Facade;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import com.CommonUtils.Utils.CompressAndDecompressUtils.ECompressAndDecompress;
import com.CommonUtils.Utils.CompressAndDecompressUtils.SubSystem.ZipUtil;
import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;

/**已废弃，请使用cn.hutool.core.util.ZipUtil
 * @deprecated
 * */
@Deprecated(since="已废弃，请使用cn.hutool.core.util.ZipUtil")
public final class CompressAndDecompressUtil 
{
	private CompressAndDecompressUtil() {}
	
	public static void compress(final ECompressAndDecompress eCompressAndDecompress, final Path zipPath, final Path ... sourcePaths) throws CompressAndDecompressUtilException
	{
		if (ArrayUtil.isArrayEmpty(sourcePaths))
		{ return; }
		
		Collection<File> sourceFiles = new ArrayList<>();
		for (Path sourcePath : sourcePaths)
		{ sourceFiles.add(sourcePath.toFile()); }
		
		compress(eCompressAndDecompress, zipPath.toFile(), sourceFiles.toArray(new File[sourceFiles.size()]));
	}
	
	public static void compress(final ECompressAndDecompress eCompressAndDecompress, final String zipFilePath, final String ... sourceFilePaths) throws CompressAndDecompressUtilException
	{
		if (ArrayUtil.isArrayEmpty(sourceFilePaths))
		{ return; }
		
		Collection<File> sourceFiles = new ArrayList<>();
		for (String sourceFilePath : sourceFilePaths)
		{ sourceFiles.add(new File(sourceFilePath)); }
		
		compress(eCompressAndDecompress, new File(zipFilePath), sourceFiles.toArray(new File[sourceFiles.size()]));
	}
	
	public static void compress(final ECompressAndDecompress eCompressAndDecompress, final File compressFile, final File ... sourceFiles) throws CompressAndDecompressUtilException
	{
		switch (eCompressAndDecompress)
		{
			case ZIP:
				ZipUtil.compress(compressFile, sourceFiles);
				break;
			default:
				throw new CompressAndDecompressUtilException("需要压缩的文件类型不正确，无法处理");
		}
	}
	
	public static void decompress(final Path srcPath, final Path saveDirectoryPath, final String charsetName) throws CompressAndDecompressUtilException
	{ decompress(srcPath.toFile(), saveDirectoryPath.toFile(), charsetName); }
	
	public static void decompress(final String srcFilePath, final String saveFileDirectoryPath, final String charsetName) throws CompressAndDecompressUtilException
	{ decompress(new File(srcFilePath), new File(saveFileDirectoryPath), charsetName); }
	
	public static void decompress(final File srcFile, final File saveFileDirectory, final String charsetName) throws CompressAndDecompressUtilException
	{
		if (!FileUtil.isFile(srcFile))
		{ throw new CompressAndDecompressUtilException("源文件有问题，可能是对象为空、文件不存在或不是文件类型"); }
		
		String fileType = FileTypeUtil.getType(srcFile);
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
						throw new CompressAndDecompressUtilException("需要解压缩压缩的文件类型不正确，无法处理");
				}
			}
		}
	}
	
	private static class CompressAndDecompressUtilException extends Exception
	{
		private static final long serialVersionUID = -6184693441720472594L;

		private CompressAndDecompressUtilException(final String message)
		{ super(message); }
	}
}