package com.CommonUtils.Utils.IOUtils;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class IOContants 
{
	private IOContants() {}

	
	public static final Map<String, String> fileType = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 4829601666788448368L;
		{
			put("jpg", "FFD8FF"); // JPEG (jpg)
			put("png", "89504E47"); // PNG (png)
			put("gif", "47494638"); // GIF (gif)
			put("tif", "49492A00"); // TIFF (tif)
			put("bmp", "424D"); // Windows Bitmap (bmp)
			put("dwg", "41433130"); // CAD (dwg)
			put("html", "68746D6C3E"); // HTML (html)
			put("rtf", "7B5C727466"); // Rich Text Format (rtf)
			put("xml", "3C3F786D6C");
			put("zip", "504B0304");
			put("rar", "52617221");
			put("psd", "38425053"); // Photoshop (psd)
			put("eml", "44656C69766572792D646174653A"); // Email[thorough only] (eml)
			put("dbx", "CFAD12FEC5FD746F");// Outlook Express (dbx)
			put("pst", "2142444E"); // Outlook (pst)
			put("xls", "D0CF11E0"); // MS Word
			put("xlsx", "504B0304"); //MS XLSX
			put("doc", "D0CF11E0"); // MS Excel 注意：word 和 excel的文件头一样
			put("mdb", "5374616E64617264204A"); // MS Access (mdb)
			put("wpd", "FF575043"); // WordPerfect (wpd)
			put("eps", "252150532D41646F6265");
			put("ps", "252150532D41646F6265");
			put("pdf", "255044462D312E"); // Adobe Acrobat (pdf)
			put("qdf", "AC9EBD8F"); // Quicken(qdf)
			put("wav", "57415645"); // Wave (wav)
			put("avi", "41564920");
			put("ram", "2E7261FD"); // Real Audio (ram)
			put("rm", "2E524D46"); // Real Media (rm)
			put("mpg", "000001BA"); //
			put("mov", "6D6F6F76"); // Quicktime (mov)
			put("asf", "3026B2758E66CF11"); // Windows Media (asf)
			put("mid", "4D546864"); // MIDI (mid)
		}
	};
	
	/**此常量用于达到多少数据量的时候，就可以进行写入或读取操作*/
	public static final int ioFetchSize = 10000;
}