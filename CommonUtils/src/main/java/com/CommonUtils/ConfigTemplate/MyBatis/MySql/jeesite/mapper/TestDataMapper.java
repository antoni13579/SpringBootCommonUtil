package com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.mapper;

import com.CommonUtils.ConfigTemplate.MyBatis.MySql.jeesite.entity.TestData;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 我的数据库中test_data
 * @Author: jeecg-boot
 * @Date:   2019-09-17
 * @Version: V1.0
 */
@DS("myDataSource")
public interface TestDataMapper extends BaseMapper<TestData> {

}
