package com.CommonUtils.Utils.CommonUtils;

/**
 * 已过时，请使用com.baomidou.mybatisplus.core.toolkit.IdWorker这个类实现分布式ID
 * @deprecated
 * */
@Deprecated(since="已过时，请使用com.baomidou.mybatisplus.core.toolkit.IdWorker这个类实现分布式ID")
public final class SnowflakeIdFactory 
{
	//开始时间戳（对应的日期为：2010-11-04 09:42:54）
	private static final long TWEPOCH = 1288834974657L;
	
	//机器ID所占的位数
    private static final long WORKER_ID_BITS = 5L;
    
    //数据标识ID所占的位数
    private static final long DATA_CENTER_ID_BITS = 5L;
    
    //支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    
    //支持的最大数据标识id，结果是31
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);
    
    //序列在id中占的位数
    private static final long SEQUENCE_BITS = 12L;
    
    //机器ID向左移12位
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    
    //数据标识id向左移17位(12+5)
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    //时间截向左移22位(5+5+12)
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    
    //生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
 
    //工作机器ID(0~31)
    private long workerId;
    
    //数据中心ID(0~31)
    private long datacenterId;
    
    //毫秒内序列(0~4095)
    private long sequence = 0L;
    
    //上次生成ID的时间截
    private long lastTimestamp = -1L;
    
    /**
     * @param workerId 机器ID
     * @param datacenterId 机房ID
     * */
    public SnowflakeIdFactory(final long workerId, final long datacenterId) 
    {
        if (workerId > MAX_WORKER_ID || workerId < 0) 
        { throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID)); }
        
        if (datacenterId > MAX_DATA_CENTER_ID || datacenterId < 0) 
        { throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID)); }
        
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    public synchronized long nextId() 
    {
        long timestamp = System.currentTimeMillis();
        
        ////如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < this.lastTimestamp) 
        {
        	//服务器时钟被调整了,ID生成器停止服务.
            throw new SnowflakeIdFactoryRuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        }
        
        ////如果是同一时间生成的，则进行毫秒内序列
        if (this.lastTimestamp == timestamp) 
        {
            this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
            
            //毫秒内序列溢出
            if (this.sequence == 0) 
            { timestamp = tilNextMillis(this.lastTimestamp); }
        }
        //时间戳改变，毫秒内序列重置
        else 
        { this.sequence = 0L; }
 
        //上次生成ID的时间截
        this.lastTimestamp = timestamp;
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) | (this.datacenterId << DATA_CENTER_ID_SHIFT) | (this.workerId << WORKER_ID_SHIFT) | this.sequence;
    }
    
    /**
     	阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(final long lastTimestamp) 
    {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) 
        { timestamp = System.currentTimeMillis(); }
        
        return timestamp;
    }
    
    private static class SnowflakeIdFactoryRuntimeException extends RuntimeException
    {
		private static final long serialVersionUID = 2966428640071337151L;

		private SnowflakeIdFactoryRuntimeException(final String message)
    	{ super(message); }
    }
}