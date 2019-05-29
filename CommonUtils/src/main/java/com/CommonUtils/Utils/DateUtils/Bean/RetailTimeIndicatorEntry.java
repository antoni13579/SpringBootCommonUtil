package com.CommonUtils.Utils.DateUtils.Bean;

import java.util.Date;

import com.CommonUtils.Utils.DateUtils.DateContants;
import com.CommonUtils.Utils.DateUtils.DateUtil;
import com.CommonUtils.Utils.DateUtils.RetailTimeIndicator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public final class RetailTimeIndicatorEntry 
{
	private RetailTimeIndicator retailTimeIndicator;
	
	private Date tyBussinessDate;
	private Date lyBussinessDate;
	
	private Date tyStartDate;
	private Date tyEndDate;
	
	private Date lyStartDate;
	private Date lyEndDate;
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("时间类型为");
		sb.append(this.retailTimeIndicator.name());
		sb.append("，");
		
		sb.append("ty时间为");
		sb.append(DateUtil.formatDateToStr(this.tyBussinessDate, DateContants.DATE_FORMAT_1));
		sb.append("，");
		
		sb.append("ly时间为");
		sb.append(DateUtil.formatDateToStr(this.lyBussinessDate, DateContants.DATE_FORMAT_1));
		sb.append("，");
		
		sb.append("ty开始时间为");
		sb.append(DateUtil.formatDateToStr(this.tyStartDate, DateContants.DATE_FORMAT_1));
		sb.append("，");
		
		sb.append("ty结束时间为");
		sb.append(DateUtil.formatDateToStr(this.tyEndDate, DateContants.DATE_FORMAT_1));
		sb.append("，");
		
		sb.append("ly开始时间为");
		sb.append(DateUtil.formatDateToStr(this.lyStartDate, DateContants.DATE_FORMAT_1));
		sb.append("，");
		
		sb.append("ly结束时间为");
		sb.append(DateUtil.formatDateToStr(this.lyEndDate, DateContants.DATE_FORMAT_1));
		sb.append("。");
		return sb.toString();
	}
}