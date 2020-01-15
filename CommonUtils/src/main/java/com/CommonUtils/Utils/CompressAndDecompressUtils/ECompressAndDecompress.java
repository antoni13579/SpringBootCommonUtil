package com.CommonUtils.Utils.CompressAndDecompressUtils;

import lombok.Getter;
import lombok.ToString;

/**已废弃，请使用cn.hutool.core.util.ZipUtil
 * @deprecated
 * */
@Deprecated(since="已废弃，请使用cn.hutool.core.util.ZipUtil")
@ToString
@Getter
public enum ECompressAndDecompress 
{
	ZIP("zip");
	
	private final String compressAndDecompressType;
	
	private ECompressAndDecompress(final String compressAndDecompressType)
	{ this.compressAndDecompressType = compressAndDecompressType; }
}