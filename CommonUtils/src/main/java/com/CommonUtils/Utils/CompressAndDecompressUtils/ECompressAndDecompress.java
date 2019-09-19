package com.CommonUtils.Utils.CompressAndDecompressUtils;

import lombok.Getter;
import lombok.ToString;

/**已废弃，请使用cn.hutool.core.util.ZipUtil*/
@Deprecated
@ToString
@Getter
public enum ECompressAndDecompress 
{
	ZIP("zip");
	
	private final String ECompressAndDecompress;
	
	private ECompressAndDecompress(final String ECompressAndDecompress)
	{ this.ECompressAndDecompress = ECompressAndDecompress; }
}