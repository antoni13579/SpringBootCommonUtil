package com.CommonUtils.Utils.OfficeUtils.ExcelUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.URLEncoder;
import cn.hutool.http.Header;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public final class ExcelUtil 
{	
	/**建议统一使用com.CommonUtils.Utils.NetworkUtils.HttpUtils.HttpUtil.downloadResponse相关方法解决*/
	public static void exportExcel(final HttpServletRequest request, final HttpServletResponse response, final File outputFile)
	{
		OutputStream bos = null;
		OutputStream os = null;
		InputStream fis = null;
		InputStream bis = null;
		try
		{
			request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("html/text");
            os = response.getOutputStream();		// 取得输出流
            bos = new BufferedOutputStream(os);		//缓冲输出流
            response.reset();						// 清空输出流
            
            response.setHeader(Header.CONTENT_DISPOSITION.toString(), "attachment; filename=" + new URLEncoder().encode(outputFile.getName(), StandardCharsets.UTF_8));		// 设定输出文件头
            response.setContentType("application/msexcel");																			// 定义输出类型
            
            fis = new FileInputStream(outputFile);
			bis = new BufferedInputStream(fis);		// 缓冲流
			
			byte[] data = new byte[1024];
            int bytes = 0;
            while ((bytes = bis.read(data, 0, data.length)) != -1) 
            { bos.write(data, 0, bytes); }
            bos.flush();
		}
		catch (Exception e)
		{ log.error("输出文件信息内容给response出异常，异常原因为：", e); }
		finally
		{			
			IoUtil.close(bos);
			IoUtil.close(os);
			IoUtil.close(bis);
			IoUtil.close(fis);
		}
	}
	
	/**建议统一使用com.CommonUtils.Utils.NetworkUtils.HttpUtils.HttpUtil.downloadResponse相关方法解决*/
	public static void exportExcel(final HttpServletRequest request, final HttpServletResponse response, final String outputFile)
	{ exportExcel(request, response, new File(outputFile)); }
	
	/**建议统一使用com.CommonUtils.Utils.NetworkUtils.HttpUtils.HttpUtil.downloadResponse相关方法解决*/
	public static void exportExcel(final HttpServletRequest request, final HttpServletResponse response, final Path outputFile)
	{ exportExcel(request, response, outputFile.toFile()); }
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static String getCellValueStr(final Cell cell)
	{
		String cellValue = "";
		if (null != cell)
		{
			switch (cell.getCellTypeEnum()) 
			{
				case STRING:
					if (!StringUtil.isStrEmpty(cell.getStringCellValue()))
					{
						cellValue = null;
						cellValue = cell.getStringCellValue().trim();
					}
					
					break;
				case NUMERIC:
					cellValue = null;
					cellValue = Double.toString(cell.getNumericCellValue());
					break;
				case FORMULA:
					cellValue = null;
					cellValue = cell.getCellFormula();
					break;
				case BLANK:
					break;
				case BOOLEAN:
					cellValue = null;
					cellValue = Boolean.toString(cell.getBooleanCellValue());
					break;
				case ERROR:
					cellValue = null;
					cellValue = Byte.toString(cell.getErrorCellValue());
					break;
				default:
					break;
			}
		}
		return cellValue;
	}
	*/
	
	/**
	 * 获取单元格数据，可兼容xls与xlsx版本
	 * */
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static Object getCellValue(final Cell cell) 
	{
		Object cellValue = new Object();
		if (null != cell)
		{
			switch (cell.getCellTypeEnum()) 
			{
				case STRING:
					if (!StringUtil.isStrEmpty(cell.getStringCellValue()))
					{ cellValue = cell.getStringCellValue().trim(); }
					else
					{ cellValue = ""; }
					break;
				case NUMERIC:
					cellValue = cell.getNumericCellValue();
					break;
				case FORMULA:
					cellValue = cell.getCellFormula();
					break;
				case BLANK:
					cellValue = "";
					break;
				case BOOLEAN:
					cellValue = cell.getBooleanCellValue();
					break;
				case ERROR:
					cellValue = cell.getErrorCellValue();
					break;
				default:
					cellValue = "";
					break;
			}
		}
		return cellValue;
	}
	*/
	
	/**
	 * 设置单元格数据，可兼容xls与xlsx版本，此重载版用于Cell重新设置值
	 * */
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static void setCellValue(final Cell cell)
	{
		switch (cell.getCellTypeEnum())
    	{
    		case STRING:
    			cell.setCellValue(cell.getStringCellValue());
    			break;
    		case NUMERIC:
    			cell.setCellValue(cell.getNumericCellValue());
    			break;
    		case FORMULA:
    			cell.setCellFormula(cell.getCellFormula());
    			break;
    		case BLANK:
    			cell.setCellValue("");
    			break;
    		case BOOLEAN:
    			cell.setCellValue(cell.getBooleanCellValue());
    			break;
    		case ERROR:
    			cell.setCellErrorValue(cell.getErrorCellValue());
    			break;
    		default:
    			cell.setCellValue("");
    			break;
    	}
	}
	*/
	
	/**
	 * 设置单元格数据，可兼容xls与xlsx版本，此重载版用于在Map获取值并赋值到Cell中
	 * */
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static void setCellValue(final Cell cell, 
									final Map<String, Object> dataValue, 
									final String key) 
	{
		Pattern pattern = Pattern.compile(StringContants.PATTERN_2);			//用于判断是否为数字的正则表达式
		switch (cell.getCellTypeEnum()) 
		{
			case STRING:
				cell.setCellValue("");
				cell.setCellType(CellType.STRING);
				String valueString = MapUtils.getString(dataValue, key, "");
				cell.setCellValue(valueString);
				break;
			case NUMERIC:
				cell.setCellValue(0);
				cell.setCellType(CellType.NUMERIC);
				String value = MapUtils.getString(dataValue, key, "");
				if (pattern.matcher(value).matches())
				{ cell.setCellValue(MapUtils.getDouble(dataValue, key, 0d)); }
				else
				{ cell.setCellValue(value); }
				
				break;
			case FORMULA:
				cell.setCellFormula("");
				cell.setCellType(CellType.FORMULA);
				cell.setCellFormula(MapUtils.getString(dataValue, key, ""));
				break;
			case BLANK:
				cell.setCellValue("");
				cell.setCellType(CellType.BLANK);
				break;
			case BOOLEAN:
				cell.setCellValue(true);
				cell.setCellType(CellType.BOOLEAN);
				cell.setCellValue(MapUtils.getBoolean(dataValue, key, true));
				break;
			case ERROR:
				cell.setCellErrorValue("0".getBytes()[0]);
				cell.setCellType(CellType.ERROR);
				cell.setCellErrorValue(MapUtils.getByte(dataValue, key, "0".getBytes()[0]));
				break;
			default:
				cell.setCellValue("");
				break;
		}
	}
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static String toJson(final Collection<ExcelData> records)
	{ return JSON.toJSONString(records); }
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static Collection<ExcelData> read(final File file)
	{ return read(file, null, 0, 0, 0, 0, 0, 0); }
	*/
	
	/*
	@Deprecated
	public static Collection<ExcelData> read(final File file, final String sheetName)
	{ return read(file, sheetName, 0, 0, 0, 0, 0, 0); }
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static Collection<ExcelData> read(final URL url)
	{ return read(url, null, 0, 0, 0, 0, 0, 0); }
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	public static Collection<ExcelData> read(final URL url, final String sheetName)
	{ return read(url, sheetName, 0, 0, 0, 0, 0, 0); }
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	private static Collection<ExcelData> read(final URL url, 
			  								  final String sheetName,
			  								  final int startSheet,
			  								  final int endSheet,
			  								  final int startRow, 
			  								  final int endRow,
			  								  final int startCell,
			  								  final int endCell)
	{
		Collection<ExcelData> result = null;
		try
		{
			URI uri = url.toURI();
			File file = new File(uri);
			result = read(file, sheetName, startSheet, endSheet, startRow, endRow, startCell, endCell);
		}
		catch (Exception ex)
		{
			log.error("根据url，读取Excel文件出现异常，异常原因为", ex);
			result = Collections.emptyList();
		}
		return result;
	}
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	private static Collection<ExcelData> read(final File file, 
			  								  final String sheetName,
			  								  final int startSheet,
			  								  final int endSheet,
			  								  final int startRow, 
			  								  final int endRow,
			  								  final int startCell,
			  								  final int endCell)
	{
		if (!FileUtil.isFile(file))
		{ return Collections.emptyList(); }
		
		InputStream is = null;
		Workbook workBook = null;
		List<ExcelData> result = new ArrayList<>();
		try
		{
			if (isXls(file))
			{
				is = new FileInputStream(file);
				workBook = new HSSFWorkbook(is);
			}
			else if (isXlsx(file))
			{ workBook = new XSSFWorkbook(file); }
			else
			{ throw new Exception("需要读取的并不是Excel格式的文件"); }
			
			//没有指定sheet名称
			if (StringUtil.isStrEmpty(sheetName))
			{				
				int tmpEndSheet = endSheet <= 0 ? workBook.getNumberOfSheets() : endSheet;
				
				//迭代每个sheet
				for (int sheetNum = startSheet; sheetNum < tmpEndSheet; sheetNum++)
				{
					Sheet sheet = workBook.getSheetAt(sheetNum);
					if (null == sheet) { continue; }
					
					ExcelData excelData = readExcelCommonHandler(sheet, startRow, endRow, startCell, endCell);
					if (null != excelData) { result.add(excelData); }
				}
			}
			
			//指定了sheet名称
			else
			{
				Sheet sheet = workBook.getSheet(sheetName);
				if (null != sheet)
				{
					ExcelData excelData = readExcelCommonHandler(sheet, startRow, endRow, startCell, endCell);
					if (null != excelData) { result.add(excelData); }
				}
			}
		}
		catch (Exception ex)
		{ log.error("读取Excel文件失败，文件路径为{}，异常原因为", file.getAbsoluteFile(), ex); }
		finally
		{
			IoUtil.close(workBook);
			IoUtil.close(is);
		}
		
		return result;
	}
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	private static boolean isXls(final File file)
	{
		if (!FileUtil.isFile(file))
		{ return false; }
		
		return FileTypeUtil.getType(file).equalsIgnoreCase("xls");
	}
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	private static boolean isXlsx(final File file)
	{
		if (!FileUtil.isFile(file))
		{ return false; }
		
		return FileTypeUtil.getType(file).equalsIgnoreCase("xlsx");
	}
	*/
	
	/**请使用cn.hutool.poi包里面相关Excel工具类*/
	/*
	@Deprecated
	private static ExcelData readExcelCommonHandler(final Sheet sheet, 
			    								   final int startRow, 
			    								   final int endRow,
			    								   final int startCell,
			    								   final int endCell)
	{
		if (null != sheet)
		{
			//设置sheet的名称与初始化集合
			ExcelData result = new ExcelData().setSheetName(sheet.getSheetName())
											  .setRows(new ArrayList<>());

			int tmpEndRow = (endRow <= 0 ? sheet.getLastRowNum() : endRow);

			//遍历行
			for (int rowNum = startRow; rowNum <= tmpEndRow; rowNum++)
			{
				Row row = sheet.getRow(rowNum);
				if (null == row) { continue; }

				int tmpEndCell = (endCell <= 0 ? row.getLastCellNum() : endCell);

				//遍历列
				Map<String, Object> cells = new HashMap<>();
				for (int cellNum = startCell; cellNum <= tmpEndCell; cellNum++)
				{
					Cell cell = row.getCell(cellNum);
					if (null == cell) { continue; }

					cells.put("CELL" + String.valueOf(cellNum), getCellValue(cell));
				}

				result.getRows().add(cells);
			}

			return result;
		}

		return null;
	}
	*/
}