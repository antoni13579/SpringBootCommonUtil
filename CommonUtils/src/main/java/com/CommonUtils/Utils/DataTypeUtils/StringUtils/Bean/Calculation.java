package com.CommonUtils.Utils.DataTypeUtils.StringUtils.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 有其他工具类代替这个
 * @deprecated
 * */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Deprecated(since="有其他工具类代替这个")
public final class Calculation 
{
	private BigDecimal elementA = new BigDecimal(0);
    private BigDecimal elementB = new BigDecimal(0);
    private BigDecimal numerator = new BigDecimal(0);

    /**
     * 返回文件相似度百分比
     * */
    public BigDecimal getCountResult()
    {
    	BigDecimal result1 = this.elementA.multiply(this.elementB);
    	double result2 = Math.sqrt(result1.doubleValue());
    	BigDecimal result3 = new BigDecimal(String.valueOf(result2));
    	return this.numerator.divide(result3, 4, RoundingMode.HALF_UP)
    						 .multiply(new BigDecimal(String.valueOf(100)));
    }
}