package com.CommonUtils.Utils.CommonUtils;

import lombok.ToString;

@ToString
public class SnowflakeIdFactory 
{
	private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
 
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    /**
     * @param workerId 机器ID
     * @param datacenterId 机房ID
     * */
    public SnowflakeIdFactory(long workerId, long datacenterId) 
    {
        if (workerId > this.maxWorkerId || workerId < 0) 
        { throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId)); }
        
        if (datacenterId > this.maxDatacenterId || datacenterId < 0) 
        { throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", this.maxDatacenterId)); }
        
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    public synchronized long nextId() 
    {
        long timestamp = System.currentTimeMillis();;
        if (timestamp < this.lastTimestamp) 
        {
        	//服务器时钟被调整了,ID生成器停止服务.
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        }
        
        if (this.lastTimestamp == timestamp) 
        {
            this.sequence = (this.sequence + 1) & this.sequenceMask;
            if (this.sequence == 0) 
            { timestamp = tilNextMillis(this.lastTimestamp); }
        } 
        else 
        { this.sequence = 0L; }
 
        this.lastTimestamp = timestamp;
        long result = ((timestamp - this.twepoch) << this.timestampLeftShift) | (this.datacenterId << this.datacenterIdShift) | (this.workerId << this.workerIdShift) | this.sequence;
        return result;
    }
    
    protected long tilNextMillis(long lastTimestamp) 
    {
        long timestamp = System.currentTimeMillis();;
        while (timestamp <= lastTimestamp) 
        { timestamp = System.currentTimeMillis();; }
        
        return timestamp;
    }
}