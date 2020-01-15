package com.CommonUtils.Utils.IOUtils;

import java.io.Closeable;
import java.io.File;

import lombok.extern.slf4j.Slf4j;

/**
 * 请使用hutool相关工具类
 * @deprecated
 * */
@Deprecated(since="请使用hutool相关工具类")
@Slf4j
public final class IOUtil 
{
	private IOUtil() {}
	
	/**建议使用cn.hutool.core.io.IoUtil.close*/
	public static void closeQuietly(final Closeable ... closeables)
	{
		if (null != closeables && closeables.length > 0)
		{
			for (Closeable closeable : closeables)
			{
				if (null != closeable)
				{
					try
					{ closeable.close(); }
					catch (Exception ex)
					{ log.error("关闭IO流出现异常，异常原因为：", ex); }
				}
			}
		}
	}
	
	/**建议使用cn.hutool.core.io.FileUtil.getTmpDirPath*/
	public static String getTempFilePath()
	{ return System.getProperty("java.io.tmpdir"); }
	
	public static boolean isFileEmpty(final File file)
	{ return null == file; }
}