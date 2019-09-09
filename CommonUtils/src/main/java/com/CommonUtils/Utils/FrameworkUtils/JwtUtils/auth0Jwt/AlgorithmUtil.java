package com.CommonUtils.Utils.FrameworkUtils.JwtUtils.auth0Jwt;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class AlgorithmUtil 
{
	private AlgorithmUtil() {}
	
	//secret变量转二进制是查询HMACAlgorithm.java中getSecretBytes方法得出
	public static Optional<com.auth0.jwt.algorithms.Algorithm> getAlgorithm(final Algorithm algorithm, final String secret)
	{ return getAlgorithm(algorithm, secret.getBytes(StandardCharsets.UTF_8)); }
	
	public static Optional<com.auth0.jwt.algorithms.Algorithm> getAlgorithm(final Algorithm algorithm, final byte[] secret)
	{
		switch (algorithm)
		{
			case HMAC256:
				return Optional.ofNullable(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret));
			case HMAC384:
				return Optional.ofNullable(com.auth0.jwt.algorithms.Algorithm.HMAC384(secret));
			case HMAC512:
				return Optional.ofNullable(com.auth0.jwt.algorithms.Algorithm.HMAC512(secret));
			default:
				return Optional.ofNullable(null);
		}
	}
	
	public enum Algorithm 
	{
		HMAC256,
		HMAC384,
		HMAC512
	}
}