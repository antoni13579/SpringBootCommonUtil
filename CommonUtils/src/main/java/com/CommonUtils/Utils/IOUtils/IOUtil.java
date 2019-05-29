package com.CommonUtils.Utils.IOUtils;

import java.io.Closeable;
import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class IOUtil 
{
	private IOUtil() {}
	
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
	
	public static String getTempFilePath()
	{ return System.getProperty("java.io.tmpdir"); }
	
	public static boolean isFileEmpty(final File file)
	{
		if (null == file)
		{ return true; }
		
		return false;
	}
}