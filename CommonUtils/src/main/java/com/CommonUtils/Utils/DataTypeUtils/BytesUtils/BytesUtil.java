package com.CommonUtils.Utils.DataTypeUtils.BytesUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * 用hutool的代替
 * @deprecated
 * */
@Slf4j
@Deprecated(since="用hutool的代替")
public final class BytesUtil 
{
	private BytesUtil() {}
	
	/**建议使用cn.hutool.core.convert.Convert.toByte*/ 
	public static <T> byte getByte(final T obj) throws BytesUtilException
	{
		if (obj instanceof Byte)
		{ return Byte.parseByte(obj.toString()); }
		else
		{ throw new BytesUtilException("无法转换为byte类型"); }
	}
	
	/**字节数组转换为对象，请使用cn.hutool.core.util.ObjectUtil.deserialize或unserialize*/
	public static <T> T fromBytes(byte[] bytes)
	{
		T obj = null;
        try
        (
        		ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
        		ObjectInputStream ois = new ObjectInputStream (bis);
        )
        { obj = Convert.convert(new TypeReference<T>() {}, ois.readObject()); } 
        catch (Exception ex) 
        { log.error("字节数组转换为对象出现异常，异常原因为：{}", ex); } 
        
        return obj;
	}
	
	/**
	 * 对象转换为字节数组，请使用cn.hutool.core.util.ObjectUtil.serialize
	 * */
	public static <T> byte[] toBytes(T obj)
	{
		byte[] bytes = null;
        try
        (
        		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        		ObjectOutputStream oos = new ObjectOutputStream(bos);
        )
        {
            oos.writeObject(obj);            
            oos.flush();
            bytes = bos.toByteArray();
        } 
        catch (Exception ex) 
        { log.error("对象转换为字节数组出现异常，异常原因为：{}", ex); }
		
		return bytes;
	}
	
	/**byte[]与转换为int，建议使用cn.hutool.core.convert.Convert.bytesToInt*/ 
	public static int byteArrayToInt(byte[] b)
	{ return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24; }  
	
	/**int转换为byte[]，建议使用cn.hutool.core.convert.Convert.intToBytes*/ 
	public static byte[] intToByteArray(int a) 
	{ return new byte[] { (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF) }; }
	
	/**int转换为byte，建议使用cn.hutool.core.convert.Convert.intToByte*/ 
	public static byte intToByte(int x) 
	{ return (byte) x; }  
	
	/**byte转换为int，建议使用cn.hutool.core.convert.Convert.byteToUnsignedInt*/
	public static int byteToInt(byte b) 
	{  
	    //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值  
	    return b & 0xFF;
	}

	/**long转换为byte[]，建议使用cn.hutool.core.convert.Convert.longToBytes*/
	public static byte[] longToByteArray(long x)
	{
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.putLong(0, x);  
	    return buffer.array();
	}  
	
	/**byte[]转换为long，建议使用cn.hutool.core.convert.Convert.bytesToLong*/
	public static long byteArrayToLong(byte[] bytes)
	{
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();//need flip
		return buffer.getLong(); 
	}
	
	/**建议使用cn.hutool.core.convert.Convert.toHex*/
	public static String byteArrayToHex(final byte[] src)
	{
		char[] buf = new char[src.length * 2];
        int index = 0;
        for(byte b : src) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = ByteContants.HEX_CHARS[b >>> 4 & 0xf];
            buf[index++] = ByteContants.HEX_CHARS[b & 0xf];
        }

        return new String(buf);
	}
	
	private static class BytesUtilException extends Exception
	{
		private static final long serialVersionUID = 1394221463995628911L;

		private BytesUtilException(final String message)
		{ super(message); }
	}
}