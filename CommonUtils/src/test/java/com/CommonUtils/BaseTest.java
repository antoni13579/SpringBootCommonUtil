package com.CommonUtils;

import org.junit.runner.RunWith;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.CommonUtils.ConfigTemplate.Startup.CommonUtilsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { CommonUtilsApplication.class })
@SpringBootConfiguration
@WebAppConfiguration
public class BaseTest 
{}