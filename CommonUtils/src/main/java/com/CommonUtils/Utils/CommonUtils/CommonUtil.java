package com.CommonUtils.Utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.CommonUtils.Utils.IOUtils.IOUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CommonUtil 
{
	private CommonUtil() {}
	
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
	 * 深度拷贝
	 * */
	public static <T> T deepCopy(final T src)
	{		
		T dest = null;
		
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try
		{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(src);
			bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
	        dest = cast(ois.readObject());
		}
		catch (Exception ex)
		{ log.error("深度拷贝异常，异常原因为：", ex); }
		finally
		{ IOUtil.closeQuietly(ois, oos, bais, baos); }
		return dest;
	}
	
	public static <T> boolean getBoolean(final T obj) throws Exception
	{
		if (obj instanceof Boolean)
		{ return (boolean)obj; }
		else
		{ throw new Exception("无法转换为boolean类型"); }
	}
	
	public static <T> int getInteger(final T obj) throws Exception
	{
		if (obj instanceof Integer)
		{ return Integer.parseInt(obj.toString()); }
		else
		{ throw new Exception("无法转换为int类型"); }
	}
	
	public static <T> short getShort(final T obj) throws Exception
	{
		if (obj instanceof Short)
		{ return Short.parseShort(obj.toString()); }
		else
		{ throw new Exception("无法转换为short类型"); }
	}
	
	public static <T> float getFloat(final T obj) throws Exception
	{
		if (obj instanceof Float)
		{ return Float.parseFloat(obj.toString()); }
		else
		{ throw new Exception("无法转换为float类型"); }
	}
	
	public static <T> long getLong(final T obj) throws Exception
	{
		if (obj instanceof Long)
		{ return Long.parseLong(obj.toString()); }
		else
		{ throw new Exception("无法转换为long类型"); }
	}
}