package com.CommonUtils.Utils.IOUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 已过时
 * @deprecated
 * */
@Deprecated(since="已过时")
public final class IOContants 
{
	private IOContants() {}
	
	protected static final Map<String, String> FILE_TYPE = new HashMap<>();

	static
	{
		FILE_TYPE.put("jpg", "FFD8FF"); // JPEG (jpg)
		FILE_TYPE.put("png", "89504E47"); // PNG (png)
		FILE_TYPE.put("gif", "47494638"); // GIF (gif)
		FILE_TYPE.put("tif", "49492A00"); // TIFF (tif)
		FILE_TYPE.put("bmp", "424D"); // Windows Bitmap (bmp)
		FILE_TYPE.put("dwg", "41433130"); // CAD (dwg)
		FILE_TYPE.put("html", "68746D6C3E"); // HTML (html)
		FILE_TYPE.put("rtf", "7B5C727466"); // Rich Text Format (rtf)
		FILE_TYPE.put("xml", "3C3F786D6C");
		FILE_TYPE.put("zip", "504B0304");
		FILE_TYPE.put("rar", "52617221");
		FILE_TYPE.put("psd", "38425053"); // Photoshop (psd)
		FILE_TYPE.put("eml", "44656C69766572792D646174653A"); // Email[thorough only] (eml)
		FILE_TYPE.put("dbx", "CFAD12FEC5FD746F");// Outlook Express (dbx)
		FILE_TYPE.put("pst", "2142444E"); // Outlook (pst)
		FILE_TYPE.put("xls", "D0CF11E0"); // MS Word
		FILE_TYPE.put("xlsx", "504B0304"); //MS XLSX
		FILE_TYPE.put("doc", "D0CF11E0"); // MS Excel 注意：word 和 excel的文件头一样
		FILE_TYPE.put("mdb", "5374616E64617264204A"); // MS Access (mdb)
		FILE_TYPE.put("wpd", "FF575043"); // WordPerfect (wpd)
		FILE_TYPE.put("eps", "252150532D41646F6265");
		FILE_TYPE.put("ps", "252150532D41646F6265");
		FILE_TYPE.put("pdf", "255044462D312E"); // Adobe Acrobat (pdf)
		FILE_TYPE.put("qdf", "AC9EBD8F"); // Quicken(qdf)
		FILE_TYPE.put("wav", "57415645"); // Wave (wav)
		FILE_TYPE.put("avi", "41564920");
		FILE_TYPE.put("ram", "2E7261FD"); // Real Audio (ram)
		FILE_TYPE.put("rm", "2E524D46"); // Real Media (rm)
		FILE_TYPE.put("mpg", "000001BA"); //
		FILE_TYPE.put("mov", "6D6F6F76"); // Quicktime (mov)
		FILE_TYPE.put("asf", "3026B2758E66CF11"); // Windows Media (asf)
		FILE_TYPE.put("mid", "4D546864"); // MIDI (mid)
	}
	
	/**此常量用于达到多少数据量的时候，就可以进行写入或读取操作*/
	public static final int IO_FETCH_SIZE = 10000;
}