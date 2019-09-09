package com.CommonUtils.Utils.SecurityUtils.EncryptionAndDecryptionUtils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public final class JasyptUtil 
{	
	public static String decrypt(final String key, final String encryptedStr)
	{
		StandardPBEStringEncryptor instance = new StandardPBEStringEncryptor();
		instance.setPassword(key);
		return instance.decrypt(encryptedStr);
	}
	
	public static String encrypt(final String key, final String needEncryptStr)
	{
		StandardPBEStringEncryptor instance = new StandardPBEStringEncryptor();
		instance.setPassword(key);
		return instance.encrypt(needEncryptStr);
	}
}