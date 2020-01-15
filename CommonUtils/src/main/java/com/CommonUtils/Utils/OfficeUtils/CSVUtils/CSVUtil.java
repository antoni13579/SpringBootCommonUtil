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
import com.opencsv.CSVReader;

import lombok.extern.slf4j.Slf4j;

/**建议使用cn.hutool.core.text.csv.CsvUtil
 * @deprecated
 * */
@Deprecated(since="建议使用cn.hutool.core.text.csv.CsvUtil")
@Slf4j
public final class CSVUtil
{
	private CSVUtil() {}

	public static Collection<String[]> read(final File file, final String encode)
	{
		if (null == file || StringUtil.isStrEmpty(encode))
    	{ return Collections.emptyList(); }
		    	
    	List<String[]> result = new ArrayList<>();
    	try
    	(
    			FileInputStream fos = new FileInputStream(file);
    			BufferedInputStream bis = new BufferedInputStream(fos);
    			InputStreamReader isr = new InputStreamReader(bis, encode);
    			BufferedReader br = new BufferedReader(isr);
    			CSVReader csvReader = new CSVReader(br);
    	)
    	{ result = csvReader.readAll(); }
    	catch (Exception ex)
    	{ log.error("读取CSV文件出现异常，文件路径为{}，异常为：", file.getAbsolutePath(), ex); }
    	
    	return result;
	}
}