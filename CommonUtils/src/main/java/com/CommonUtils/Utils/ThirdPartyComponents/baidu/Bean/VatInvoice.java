package com.CommonUtils.Utils.ThirdPartyComponents.baidu.Bean;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.CommonUtils.Utils.DateUtils.DateContants;
import com.CommonUtils.Utils.DateUtils.DateUtil;
import com.CommonUtils.Utils.StringUtils.StringContants;
import com.CommonUtils.Utils.StringUtils.StringUtil;
import com.alibaba.fastjson.JSON;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class VatInvoice 
{
	private File file;
	private String imageUrl;
	
	/**唯一的log id，用于问题定位*/
	private long logId;
	
	/**识别结果数，表示words_result的元素个数*/
	private int wordsResultNum;
	
	/** 图像方向，当detect_direction=true时存在。
		- -1:未定义，
		- 0:正向，
		- 1: 逆时针90度，
		- 2:逆时针180度，
		- 3:逆时针270度
	 * */
	private int direction;
	
	/**发票号码*/
	private String invoiceNum;
	
	/**销售方名称*/
	private String sellerName;
	
	/**税率*/
	private Collection<CommodityTaxRate> commodityTaxRate;
	
	/**销售方开户行及账号*/
	private SellerBank sellerBank;
	
	/**复核*/
	private String checker;
	
	/**开票人*/
	private String noteDrawer;
	
	/**金额*/
	private Collection<CommodityAmount> commodityAmount;
	
	/** 开票日期*/
	private InvoiceDate invoiceDate;
	
	/**税额*/
	private Collection<CommodityTax> commodityTax;
	
	/** 购方名称 */
	private String purchaserName;
	
	/** 发票名称*/
	private String invoiceTypeOrg;
	
	/**数量*/
	private Collection<CommodityNum> commodityNum;
	
	/**购方开户行及账号*/
	private PurchaserBank purchaserBank;
	
	/**备注*/
	private String remarks;
	
	/**密码区*/
	private String password;
	
	/**销售方地址及电话*/
	private SellerAddress sellerAddress;
	
	/**购买方地址及电话*/
	private PurchaserAddress purchaserAddress;
	
	/**发票代码*/
	private String invoiceCode;
	
	/**单位*/
	private Collection<CommodityUnit> commodityUnit;
	
	/**收款人*/
	private String payee;
	
	/**购方纳税人识别号*/
	private String purchaserRegisterNum;
	
	/**单价*/
	private Collection<CommodityPrice> commodityPrice;
	
	/**合计金额*/
	private BigDecimal totalAmount;
	
	/**价税合计(大写)*/
	private String amountInWords;
	
	/**价税合计(小写)*/
	private BigDecimal amountInFiguers;
	
	/**合计税额*/
	private BigDecimal totalTax;
	
	/**发票种类*/
	private String invoiceType;
	
	/**销售方纳税人识别号*/
	private String sellerRegisterNum;
	
	/**货物名称*/
	private Collection<CommodityName> commodityName;
	
	/**规格型号*/
	private Collection<CommodityType> commodityType;
	
	/**校验码*/
	private String checkCode;
	
	public String toJson()
	{ return JSON.toJSONString(this); }
	
	/** 开票日期 */
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class InvoiceDate
	{
		private String invoiceDate;
		private Date actualInvoiceDate;
		
		public InvoiceDate transferInvoiceDate() throws Exception
		{
			this.actualInvoiceDate = DateUtil.formatStrToDate(this.invoiceDate, DateContants.DATE_FORMAT_7)
											 .orElseThrow(() -> new Exception("获取回来的开票日期为空！！！"));
			return this;
		}
	}
	
	/** 税率 */
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityTaxRate
	{
		private int row;
		private String word;
		private long actualTaxRate;
		
		public CommodityTaxRate transferTaxRate()
		{
			this.actualTaxRate = Long.valueOf(this.word.replaceAll("%", ""));
			return this;
		}
	}
	
	/**金额*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityAmount
	{
		private int row;
		private String word;
		private BigDecimal actualAmount;
		
		public CommodityAmount transferAmount()
		{
			this.actualAmount = new BigDecimal(this.word);
			return this;
		}
	}
	
	/**税额*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityTax
	{
		private int row;
		private String word;
		private BigDecimal actualTax;
		
		public CommodityTax transferTax()
		{
			this.actualTax = new BigDecimal(this.word);
			return this;
		}
	}
	
	/**数量*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityNum
	{
		private int row;
		private String word;
		private long actualNum;
		
		public CommodityNum transferNum()
		{
			this.actualNum = Long.valueOf(this.word);
			return this;
		}
	}
	
	/**单位*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityUnit
	{
		private int row;
		private String word;
	}
	
	/**单价*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityPrice
	{
		private int row;
		private String word;
		private BigDecimal actualPrice;
		
		public CommodityPrice transferPrice()
		{
			this.actualPrice = new BigDecimal(this.word);
			return this;
		}
	}
	
	/**货物名称*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityName
	{
		private int row;
		private String word;
	}
	
	/**规格型号*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class CommodityType
	{
		private int row;
		private String word;
	}
	
	/**销售方开户行及账号*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class SellerBank
	{
		private String word;
		private String bankName;
		private String bankAccount;
		
		public SellerBank splitInfo()
		{
			this.bankAccount = StringUtil.searchStr(StringContants.PATTERN_8, this.word);
			this.bankName = this.word.substring(0, StringUtil.getNumberStartPos(this.word))
									 .replaceAll(",", "")
									 .replaceAll("，", "")
									 .replaceAll("、", "");
			return this;
		}
	}
	
	/**销售方地址及电话*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class SellerAddress
	{
		private String word;
		private String address;
		private String phone;
		
		public SellerAddress splitInfo()
		{
			this.address = StringUtil.searchStr(StringContants.PATTERN_11, this.word)
					 				 .replaceAll(",", "")
					 				 .replaceAll("，", "")
					 				 .replaceAll("、", "");
			this.phone = this.word.substring(StringUtil.searchStr(StringContants.PATTERN_11, this.word).length(), this.word.length());
			return this;
		}
	}
	
	/**购方开户行及账号*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class PurchaserBank
	{
		private String word;
		private String bankName;
		private String bankAccount;
		
		public PurchaserBank splitInfo()
		{
			this.bankAccount = StringUtil.searchStr(StringContants.PATTERN_8, this.word);
			this.bankName = this.word.substring(0, StringUtil.getNumberStartPos(this.word))
									 .replaceAll(",", "")
									 .replaceAll("，", "")
									 .replaceAll("、", "");
			return this;
		}
	}
	
	
	
	/**购买方地址及电话*/
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	public static class PurchaserAddress
	{
		private String word;
		private String address;
		private String phone;
		
		public PurchaserAddress splitInfo()
		{
			this.address = StringUtil.searchStr(StringContants.PATTERN_9, this.word)
									 .replaceAll(",", "")
									 .replaceAll("，", "")
									 .replaceAll("、", "");
			
			this.phone = this.word.substring(StringUtil.searchStr(StringContants.PATTERN_9, this.word).length(), this.word.length());
			return this;
		}
	}
}