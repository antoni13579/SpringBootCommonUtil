package com.CommonUtils.Utils.CompressAndDecompressUtils;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum ECompressAndDecompress 
{
	ZIP("zip");
	
	private final String ECompressAndDecompress;
	
	private ECompressAndDecompress(final String ECompressAndDecompress)
	{ this.ECompressAndDecompress = ECompressAndDecompress; }
}