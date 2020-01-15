package com.CommonUtils.Utils.NetworkUtils.HttpUtils.Bean;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.AllArgsConstructor;

/**
 * 已过时
 * @deprecated
 * */
@Deprecated(since="已过时")
@Getter
@Setter
@Accessors(chain = true)
@ToString
@AllArgsConstructor
public class DownloadFileInfo 
{
	//提供给请求方下载文件的名称
	private String fileName;
	
	//需要下载的文件路径信息
	private File file;
}