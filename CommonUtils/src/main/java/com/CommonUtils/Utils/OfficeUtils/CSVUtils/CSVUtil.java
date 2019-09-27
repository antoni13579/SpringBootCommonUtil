package com.CommonUtils.Utils.OfficeUtils.CSVUtils;

/**建议使用cn.hutool.core.text.csv.CsvUtil*/
@Deprecated
public final class CSVUtil
{
	private CSVUtil() {}
	/*
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
    	{
    		IoUtil.close(csvReader);
    		IoUtil.close(br);
    		IoUtil.close(isr);
    		IoUtil.close(bis);
    		IoUtil.close(fos);
    	}
    	
    	return result;
	}
	*/
}