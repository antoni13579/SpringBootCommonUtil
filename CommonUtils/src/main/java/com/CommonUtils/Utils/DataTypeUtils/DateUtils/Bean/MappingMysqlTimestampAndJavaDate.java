package com.CommonUtils.Utils.DataTypeUtils.DateUtils.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public final class MappingMysqlTimestampAndJavaDate 
{
	private double mysqlTimestamp;
	private long mysqlTimestampForInteger = 0L;
	private long mysqlTimestampForSmallNumber = 0L;
	
	private long rate = 1000L;
	
	private Date javaDate;
	
	/**mysql的timestamp转换为java的date*/
	public MappingMysqlTimestampAndJavaDate(final double mysqlTimestamp)
	{
		this.mysqlTimestamp = mysqlTimestamp;
		
		String[] arr = new BigDecimal(this.mysqlTimestamp)
				.setScale(4, RoundingMode.HALF_UP)
				.toPlainString()
				.split("\\.");
		
		if (arr.length == 2)
		{ this.mysqlTimestampForSmallNumber = Long.valueOf(arr[1]); }
		
		this.mysqlTimestampForInteger = Long.valueOf(arr[0]);
		
		this.javaDate = new Date(this.mysqlTimestampForInteger * this.rate + this.mysqlTimestampForSmallNumber);
	}
	
	/**java的date转换为mysql的timestamp*/
	public MappingMysqlTimestampAndJavaDate(final Date javaDate)
	{
		this.javaDate = javaDate;		
		this.mysqlTimestamp =  this.javaDate.getTime() / this.rate;
		this.mysqlTimestampForInteger = (long) this.mysqlTimestamp;
	}
}