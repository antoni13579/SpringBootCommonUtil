package com.CommonUtils.Utils.IdCardUtils.Bean;

import java.util.Date;
import java.util.Optional;

import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateContants;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;
import com.CommonUtils.Utils.IdCardUtils.EGender;
import com.CommonUtils.Utils.IdCardUtils.IdCardContants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**中国身份证号码信息拆解*/
@Getter
@Setter
@Accessors(chain = true)
@ToString
public final class ChinaIdCard
{
	private ChinaIdCardExtract chinaIdCardExtract;
	
	//用于存储实际的身份证号码
	private String idCard;
	
	//用于把birthYear、birthMonth、birthDay转换为实际的出生日期
	private Optional<Date> birth;
	
	//根据gender，判断出实际的性别
	private EGender eGender;
	
	//provinceCode、cityCode、districtCode组装成的行政区划代码
	private String administrativeAreaCode;
	
	private String provinceName;
	
	public ChinaIdCard(final String idCard)
	{
		this.idCard = idCard;
		this.chinaIdCardExtract = new ChinaIdCardExtract(this.idCard);
		
		this.birth = DateUtil.formatStrToDate(StringUtil.substr(this.idCard, 6, 8), DateContants.DATE_FORMAT_1);
		this.eGender = Integer.parseInt(this.chinaIdCardExtract.getGender()) % 2 != 0 ? EGender.MALE : EGender.FEMALE;
		this.administrativeAreaCode = this.idCard.substring(0, 6);
		this.provinceName = IdCardContants.CITY_CODES.getOrDefault(Integer.parseInt(this.idCard.substring(0, 2)), "");
	}
	
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public final class ChinaIdCardExtract
	{
		//前1/2位数字代表所在的省份代码
		private String provinceCode;
		
		//前3/4位数字代表所在城市代码。
		private String cityCode;
		
		//前5/6位数字代表所在区县代码。
		private String districtCode;
		
		//出生年份
		private String birthYear;
		
		//出生月份
		private String birthMonth;
		
		//出生日子
		private String birthDay;
		
		//第15/16位数字代表所在地派出所代码
		private String policeCode;
		
		//第17位表示性别，单数表示男性，双数表示女性。
		private String gender;
		
		//第18位是校验码，用0—9表示，10就用X表示
		private String checkCode;
		
		public ChinaIdCardExtract(final String idCard)
		{
			this.provinceCode = idCard.substring(0, 2);
			this.cityCode = idCard.substring(2, 4);
			this.districtCode = idCard.substring(4, 6);
			this.birthYear = idCard.substring(6, 10);
			this.birthMonth = idCard.substring(10, 12);
			this.birthDay = StringUtil.substr(idCard, 12, 2);
			this.policeCode = idCard.substring(14, 16);
			this.gender = idCard.substring(16, 17);
			this.checkCode = idCard.substring(17, 18).toUpperCase();
		}
	}
}