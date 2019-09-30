package com.CommonUtils.Utils.SystemUtils.RemoteUtils.Ssh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public final class ParseCommandResult
{	
	/**
	 * 运行了df -h命令后，解析其结果，获取结果
	 * */
	public static List<Map<String, String>> getDFHCommandResult(final List<String> executeRecords)
	{
		List<Map<String, String>> records = new ArrayList<Map<String, String>>();
		if (!CollUtil.isEmpty(executeRecords))
		{
			int skipRow = 1;
			for (String executeRecord : executeRecords)
			{
				//跳过第一行的记录，这个是不需要的
				if (skipRow == 1)
				{
					skipRow++;
					continue;
				}
				
				String addSeparatorStr = executeRecord.replaceAll(" ", "，");
				String addSeparatorStrArray[] = addSeparatorStr.split("，");
				int indx = 0;
				Map<String, String> record = new HashMap<String, String>();
				for (String str : addSeparatorStrArray)
				{
					if (!StrUtil.isEmptyIfStr(str))
					{
						if (0 == indx)
						{ record.put("FILE_SYSTEM", str); }
						else if (1 == indx)
						{ record.put("SIZE", str); }
						else if (2 == indx)
						{ record.put("USED", str); }
						else if (3 == indx)
						{ record.put("AVAIL", str); }
						else if (4 == indx)
						{ record.put("USE_PERCENT", str); }
						else if (5 == indx)
						{ record.put("MOUNTED_ON", str); }
						
						//如果出现indx > 5的情况，直接返回结果，程序结束
						else
						{ return records; }
						
						indx++;
						if (indx > 5)
						{ indx = 0; }
					}
				}
				records.add(record);
			}
		}
		return records;
	}
	
	/**
	 * 运行了ls -lh命令后，解析其结果，获取结果，以
	 * */
    public static List<Map<String, String>> getLSLHCommandResult(final List<String> executeRecords)
    {
    	List<Map<String, String>> records = new ArrayList<Map<String, String>>();
    	if (!CollUtil.isEmpty(executeRecords))
    	{
    		int firstRow = 1;
    		for (String executeRecord : executeRecords)
    		{
    			//第一行为总用量，需要获取
    			if (firstRow == 1)
				{
    				String addSeparatorStr = executeRecord.replaceAll(" ", "，");
        			String addSeparatorStrArray[] = addSeparatorStr.split("，");
        			if (!ArrayUtil.isEmpty(addSeparatorStrArray))
        			{
        				records.add
            			(
            					new HashMap<String, String>()
            					{
    								private static final long serialVersionUID = 4551007307891451307L;
    								{ put("TOTAL_USED", addSeparatorStrArray[1]); }
            					}
            		    );
        			}
    				firstRow++;
					continue;
				}
    			
    			String addSeparatorStr = executeRecord.replaceAll(" ", "，");
    			String addSeparatorStrArray[] = addSeparatorStr.split("，");
    			int indx = 0;
    			Map<String, String> record = new HashMap<String, String>();
    			for (String str : addSeparatorStrArray)
    			{
    				if (!StrUtil.isEmptyIfStr(str))
    				{
    					if (0 == indx)
						{ record.put("FILE_ATTRIBUTES", str); }
						else if (1 == indx)
						{ record.put("COUNT", str); }
						else if (2 == indx)
						{ record.put("OWNER", str); }
						else if (3 == indx)
						{ record.put("GROUP", str); }
						else if (4 == indx)
						{ record.put("SIZE", str); }
						else if (5 == indx)
						{ record.put("MONTH", str); }
						else if (6 == indx)
						{ record.put("DAY", str); }
						else if (7 == indx)
						{ record.put("TIME", str); }
						else if (8 == indx)
						{ record.put("FILE_OR_DIRECTORY_NAME", str); }
						
						//如果出现indx > 8的情况，直接返回结果，程序结束
						else
						{ return records; }
						
						indx++;
						if (indx > 8)
						{ indx = 0; }
    				}
    			}
    			records.add(record);
    		}
    	}
    	return records;
    }
}