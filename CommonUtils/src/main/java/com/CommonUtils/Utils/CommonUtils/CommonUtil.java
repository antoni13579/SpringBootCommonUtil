package com.CommonUtils.Utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.CommonUtils.Utils.IOUtils.IOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 已过期
 * @deprecated
 * */
@Slf4j
@Deprecated(since="已过期")
public final class CommonUtil 
{
	private CommonUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.convert*/
	@SuppressWarnings("unchecked")
	public static <T> T cast(final Object obj)
	{
		T result = null;
		try { result = (T) obj; }
		catch (Exception ex) { log.error("强制转换出现异常，异常原因为：", ex); }
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T1, T2> Class<T2> cast(final Class<T1> clazz)
	{
		Class<T2> result = null;
		try { result = (Class<T2>)clazz; }
		catch (Exception ex) { log.error("强制转换出现异常，异常原因为：", ex); }
		return result;
	}
	
	/**
	 * 深度拷贝，由于别人已经实现了，建议使用cn.hutool.core.util.ObjectUtil.cloneByStream，代码逻辑都是一样的
	 * */
	public static <T> T deepCopy(final T src)
	{
		T dest = null;
		
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try
		(
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
		)
		{
			oos.writeObject(src);
			bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
	        dest = cast(ois.readObject());
		}
		catch (Exception ex)
		{ log.error("深度拷贝异常，异常原因为：", ex); }
		finally
		{ IOUtil.closeQuietly(ois, bais); }
		return dest;
	}
}