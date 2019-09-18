package com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 销售报表服务器的DEMO表
 * @Author: jeecg-boot
 * @Date:   2019-09-17
 * @Version: V1.0
 */
@Data
@TableName("demo")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="demo对象", description="销售报表服务器的DEMO表")
public class Demo {
    
	/**主键ID*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
	private java.lang.String id;
	/**姓名*/
    @ApiModelProperty(value = "姓名")
	private java.lang.String name;
	/**关键词*/
    @ApiModelProperty(value = "关键词")
	private java.lang.String keyWord;
	/**打卡时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "打卡时间")
	private java.util.Date punchTime;
	/**工资*/
    @ApiModelProperty(value = "工资")
	private java.math.BigDecimal salaryMoney;
	/**奖金*/
    @ApiModelProperty(value = "奖金")
	private java.lang.Double bonusMoney;
	/**性别 {男:1,女:2}*/
    @ApiModelProperty(value = "性别 {男:1,女:2}")
	private java.lang.String sex;
	/**年龄*/
    @ApiModelProperty(value = "年龄")
	private java.lang.Integer age;
	/**生日*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
	private java.util.Date birthday;
	/**邮箱*/
    @ApiModelProperty(value = "邮箱")
	private java.lang.String email;
	/**个人简介*/
    @ApiModelProperty(value = "个人简介")
	private java.lang.String content;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;
	/**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
	private java.lang.String sysOrgCode;
}
