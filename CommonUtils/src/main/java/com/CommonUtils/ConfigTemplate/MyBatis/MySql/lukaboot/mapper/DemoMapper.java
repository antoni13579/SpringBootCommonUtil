package com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.mapper;

import com.CommonUtils.ConfigTemplate.MyBatis.MySql.lukaboot.entity.Demo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 销售报表服务器的DEMO表
 * @Author: jeecg-boot
 * @Date:   2019-09-17
 * @Version: V1.0
 */
@DS("lukabootDataSource")
public interface DemoMapper extends BaseMapper<Demo> {

}
