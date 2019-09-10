package com.CommonUtils.Utils.OfficeUtils.CSVUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;
import com.CommonUtils.Utils.IOUtils.IOUtil;
import com.opencsv.CSVReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CSVUtil
{
	private CSVUtil() {}
	
	public static Collection<String[]> read(final File file, final String encode)
	{
		if (null == file || StringUtil.isStrEmpty(encode))
    	{ return Collections.emptyList(); }
		
		FileInputStream fos = null;
    	BufferedInputStream bis = null;
    	InputStreamReader isr = null;
    	BufferedReader br = null;
    	CSVReader csvReader = null;
    	
    	List<String[]> result = new ArrayList<String[]>();
    	try
    	{
    		fos = new FileInputStream(file);
    		bis = new BufferedInputStream(fos);
    		isr = new InputStreamReader(bis, encode);
    		br = new BufferedReader(isr);
    		csvReader = new CSVReader(br);
    		result = csvReader.readAll();
    	}
    	catch (Exception ex)
    	{ log.error("读取CSV文件出现异常，文件路径为{}，异常为：", file.getAbsolutePath(), ex); }
    	finally
    	{ IOUtil.closeQuietly(csvReader, br, isr, bis, fos); }
    	
    	return result;
	}
}