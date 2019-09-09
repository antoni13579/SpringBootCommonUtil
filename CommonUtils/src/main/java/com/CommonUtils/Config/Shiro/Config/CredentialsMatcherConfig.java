package com.CommonUtils.Config.Shiro.Config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

public final class CredentialsMatcherConfig 
{
	private CredentialsMatcherConfig() {}
	
	public static HashedCredentialsMatcher getHashedCredentialsMatcher(final String hashAlgorithmName, final int hashIterations)
	{
		HashedCredentialsMatcher result = new HashedCredentialsMatcher(hashAlgorithmName);
		result.setHashIterations(hashIterations);
		return result;
	}
}