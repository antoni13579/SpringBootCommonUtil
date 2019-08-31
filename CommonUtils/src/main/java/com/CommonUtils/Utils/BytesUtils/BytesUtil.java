package com.CommonUtils.Utils.BytesUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.IOUtils.IOUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BytesUtil 
{
	private BytesUtil() {}
	
	public static <T> byte getByte(final T obj) throws Exception
	{
		if (obj instanceof Byte)
		{ return Byte.parseByte(obj.toString()); }
		else
		{ throw new Exception("无法转换为byte类型"); }
	}
	
	/**字节数组转换为对象*/
	public static <T> T fromBytes(byte[] bytes)
	{
		T obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try 
        {
            bis = new ByteArrayInputStream (bytes);
            ois = new ObjectInputStream (bis);
            obj = CommonUtil.cast(ois.readObject());

        } 
        catch (Exception ex) 
        { log.error("字节数组转换为对象出现异常，异常原因为：{}", ex); } 
        finally 
        {            
            IOUtil.closeQuietly(ois);
            IOUtil.closeQuietly(bis);
        }
        
        return obj;
	}
	
	/**
	 * 对象转换为字节数组
	 * */
	public static <T> byte[] toBytes(T obj)
	{
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        
        try 
        {
        	bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } 
        catch (Exception ex) 
        { log.error("对象转换为字节数组出现异常，异常原因为：{}", ex); }
        finally 
        {            
            IOUtil.closeQuietly(oos);
            IOUtil.closeQuietly(bos);
        }
		
		return bytes;
	}
	
	/**byte[]与转换为int*/ 
	public static int byteArrayToInt(byte[] b)
	{ return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24; }  
	
	/**int转换为byte[]*/ 
	public static byte[] intToByteArray(int a) 
	{ return new byte[] { (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF) }; }
	
	/**int转换为byte*/ 
	public static byte intToByte(int x) 
	{ return (byte) x; }  
	
	/**byte转换为int*/
	public static int byteToInt(byte b) 
	{  
	    //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值  
	    return b & 0xFF;
	}

	/**long转换为byte[]*/
	public static byte[] longToByteArray(long x)
	{
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.putLong(0, x);  
	    return buffer.array();
	}  
	
	/**byte[]转换为long*/
	public static long byteArrayToLong(byte[] bytes)
	{
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();//need flip
		return buffer.getLong(); 
	}
	
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
}