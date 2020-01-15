package com.CommonUtils.Utils.FrameworkUtils.JwtUtils.auth0Jwt;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;

import cn.hutool.core.convert.Convert;

public final class VerificationUtil 
{
	private Verification verification;
	
	public VerificationUtil(final Algorithm algorithm)
	{ this.verification = JWT.require(algorithm); }
	
	public JWTVerifier getJWTVerifier()
	{ return this.verification.build(); }
	
	public VerificationUtil setTokenPayload(final Map<String, Object> customPayloadClaims) throws VerificationUtilException
	{
		Iterator<Entry<String, Object>> iter = customPayloadClaims.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, Object> entry = iter.next();
			String name = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof Boolean) 
			{ this.verification.withClaim(name, Convert.toBool(value)); }
			
			else if (value instanceof Integer) 
			{ this.verification.withClaim(name, Convert.toInt(value)); }
			
			else if (value instanceof Long) 
			{ this.verification.withClaim(name, Convert.toLong(value)); }
			
			else if (value instanceof Double) 
			{ this.verification.withClaim(name, Convert.toDouble(value)); }
			
			else if (value instanceof String) 
			{ this.verification.withClaim(name, Convert.toStr(value)); }
			
			else if (value instanceof Date) 
			{ this.verification.withClaim(name, Convert.toDate(value)); }
			
			else if (value instanceof String[]) 
			{ this.verification.withArrayClaim(name, Convert.toStrArray(value)); }
			
			else if (value instanceof Integer[]) 
			{ this.verification.withArrayClaim(name, Convert.toIntArray(value)); }
			
			else 
			{ throw new VerificationUtilException("设置jwt token自定义payload的数据类型不符合要求！！！异常的数据类型为：" + value.getClass()); }
		}
		
		return this;
	}
	
	public VerificationUtil ignoreTokenPayloadIssuedAt()
	{
		this.verification.ignoreIssuedAt();
		return this;
	}
	
	public VerificationUtil setTokenPayloadIssuer(String... issuer)
	{
		this.verification.withIssuer(issuer);
		return this;
	}

	public VerificationUtil setTokenPayloadSubject(String subject)
    {
		this.verification.withSubject(subject);
		return this;
    }

	public VerificationUtil setTokenPayloadAudience(String... audience)
    {
		this.verification.withAudience(audience);
		return this;
    }

	public VerificationUtil setTokenPayloadLeeway(long leeway)
    {
		this.verification.acceptLeeway(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadExpiresAt(long leeway)
    {
		this.verification.acceptExpiresAt(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadNotBefore(long leeway)
    {
		this.verification.acceptNotBefore(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadIssuedAt(long leeway)
    {
		this.verification.acceptIssuedAt(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadJWTId(String jwtId)
    {
		this.verification.withJWTId(jwtId);
		return this;
    }
	
	private static class VerificationUtilException extends Exception
	{
		private static final long serialVersionUID = -5456925487458024739L;

		private VerificationUtilException(final String message)
		{ super(message); }
	}
}