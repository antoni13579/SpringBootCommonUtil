package com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.entity;

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
 * @Description: 我的数据库中test_data
 * @Author: jeecg-boot
 * @Date:   2019-09-17
 * @Version: V1.0
 */
@Data
@TableName("test_data")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="test_data对象", description="我的数据库中test_data")
public class TestData {
    
	/**创建者*/
    @ApiModelProperty(value = "创建者")
	private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createDate;
	/**编号*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "编号")
	private java.lang.String id;
	/**备注信息*/
    @ApiModelProperty(value = "备注信息")
	private java.lang.String remarks;
	/**状态（0正常 1删除 2停用）*/
    @ApiModelProperty(value = "状态（0正常 1删除 2停用）")
	private java.lang.String status;
	/**区域选择*/
    @ApiModelProperty(value = "区域选择")
	private java.lang.String testAreaCode;
	/**区域名称*/
    @ApiModelProperty(value = "区域名称")
	private java.lang.String testAreaName;
	/**复选框*/
    @ApiModelProperty(value = "复选框")
	private java.lang.String testCheckbox;
	/**日期选择*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "日期选择")
	private java.util.Date testDate;
	/**日期时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "日期时间")
	private java.util.Date testDatetime;
	/**单行文本*/
    @ApiModelProperty(value = "单行文本")
	private java.lang.String testInput;
	/**机构选择*/
    @ApiModelProperty(value = "机构选择")
	private java.lang.String testOfficeCode;
	/**单选框*/
    @ApiModelProperty(value = "单选框")
	private java.lang.String testRadio;
	/**下拉框*/
    @ApiModelProperty(value = "下拉框")
	private java.lang.String testSelect;
	/**下拉多选*/
    @ApiModelProperty(value = "下拉多选")
	private java.lang.String testSelectMultiple;
	/**多行文本*/
    @ApiModelProperty(value = "多行文本")
	private java.lang.String testTextarea;
	/**用户选择*/
    @ApiModelProperty(value = "用户选择")
	private java.lang.String testUserCode;
	/**更新者*/
    @ApiModelProperty(value = "更新者")
	private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private java.util.Date updateDate;
}
