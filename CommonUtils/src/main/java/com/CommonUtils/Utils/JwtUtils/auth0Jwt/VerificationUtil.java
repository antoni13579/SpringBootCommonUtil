package com.CommonUtils.Utils.JwtUtils.auth0Jwt;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.DateUtils.DateUtil;
import com.CommonUtils.Utils.DataTypeUtils.DoubleUtils.DoubleUtil;
import com.CommonUtils.Utils.DataTypeUtils.IntegerUtils.IntegerUtil;
import com.CommonUtils.Utils.DataTypeUtils.LongUtils.LongUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Verification;

public final class VerificationUtil 
{
	private Verification verification;
	
	public VerificationUtil(final Algorithm algorithm)
	{ this.verification = JWT.require(algorithm); }
	
	public JWTVerifier getJWTVerifier()
	{ return this.verification.build(); }
	
	public VerificationUtil setTokenPayload(final Map<String, Object> customPayloadClaims) throws Exception
	{
		Iterator<Entry<String, Object>> iter = customPayloadClaims.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, Object> entry = iter.next();
			String name = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof Boolean) 
			{ this.verification.withClaim(name, CommonUtil.getBoolean(value)); }
			
			else if (value instanceof Integer) 
			{ this.verification.withClaim(name, IntegerUtil.getInteger(value)); }
			
			else if (value instanceof Long) 
			{ this.verification.withClaim(name, LongUtil.getLong(value)); }
			
			else if (value instanceof Double) 
			{ this.verification.withClaim(name, DoubleUtil.getDouble(value)); }
			
			else if (value instanceof String) 
			{ this.verification.withClaim(name, StringUtil.getString(value)); }
			
			else if (value instanceof Date) 
			{ this.verification.withClaim(name, DateUtil.getDate(value)); }
			
			else if (value instanceof String[]) 
			{ this.verification.withArrayClaim(name, ArrayUtil.getStringArray(value)); }
			
			else if (value instanceof Integer[]) 
			{ this.verification.withArrayClaim(name, ArrayUtil.getIntegerArrayForWrapperClass(value)); }
			
			else 
			{ throw new Exception("设置jwt token自定义payload的数据类型不符合要求！！！异常的数据类型为：" + value.getClass()); }
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

	public VerificationUtil setTokenPayloadLeeway(long leeway) throws IllegalArgumentException
    {
		this.verification.acceptLeeway(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadExpiresAt(long leeway) throws IllegalArgumentException
    {
		this.verification.acceptExpiresAt(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadNotBefore(long leeway) throws IllegalArgumentException
    {
		this.verification.acceptNotBefore(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadIssuedAt(long leeway) throws IllegalArgumentException
    {
		this.verification.acceptIssuedAt(leeway);
		return this;
    }

	public VerificationUtil setTokenPayloadJWTId(String jwtId)
    {
		this.verification.withJWTId(jwtId);
		return this;
    }
}