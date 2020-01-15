package com.CommonUtils.Utils.FrameworkUtils.JwtUtils.auth0Jwt;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

import cn.hutool.core.convert.Convert;

public final class BuilderUtil 
{
	private Builder builder;
	private Algorithm algorithm;
	
	public BuilderUtil(final Optional<Algorithm> algorithm)
	{
		this.builder = JWT.create();
		this.algorithm = algorithm.orElseThrow();
	}
	
	public String getToken()
	{ return this.builder.sign(this.algorithm); }
	
	public BuilderUtil setTokenPayload(final Map<String, Object> customPayloadClaims) throws BuilderUtilException
	{
		Iterator<Entry<String, Object>> iter = customPayloadClaims.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, Object> entry = iter.next();
			String name = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof Boolean) 
			{ this.builder.withClaim(name, Convert.toBool(value)); }
			
			else if (value instanceof Integer) 
			{ this.builder.withClaim(name, Convert.toInt(value)); }
			
			else if (value instanceof Long) 
			{ this.builder.withClaim(name, Convert.toLong(value)); }
			
			else if (value instanceof Double) 
			{ this.builder.withClaim(name, Convert.toDouble(value)); }
			
			else if (value instanceof String) 
			{ this.builder.withClaim(name, Convert.toStr(value)); }
			
			else if (value instanceof Date) 
			{ this.builder.withClaim(name, Convert.toDate(value)); }
			
			else if (value instanceof String[]) 
			{ this.builder.withArrayClaim(name, Convert.toStrArray(value)); }
			
			else if (value instanceof Integer[]) 
			{ this.builder.withArrayClaim(name, Convert.toIntArray(value)); }
			
			else if (value instanceof Long[]) 
			{ this.builder.withArrayClaim(name, Convert.toLongArray(value)); }
			
			else 
			{ throw new BuilderUtilException("设置jwt token自定义payload的数据类型不符合要求！！！异常的数据类型为：" + value.getClass()); }
		}
		
		return this;
	}
	
	public BuilderUtil setTokenHeader(final Map<String, Object> customHeaderClaims)
	{
		this.builder.withHeader(customHeaderClaims);
		return this;
	}
	
	public BuilderUtil setTokenHeaderKeyId(final String keyId)
	{
		this.builder.withKeyId(keyId);
		return this;
	}
	
	/**iss: jwt签发者*/
	public BuilderUtil setTokenPayloadIssuer(final String issuer)
	{
		this.builder.withIssuer(issuer);
		return this;
	}
	
	/**sub: jwt所面向的用户*/
	public BuilderUtil setTokenPayloadSubject(final String subject) 
	{
		this.builder.withSubject(subject);
		return this;
	}

	/**aud: 接收jwt的一方*/
    public BuilderUtil setTokenPayloadAudience(final String... audience) 
    {
    	this.builder.withAudience(audience);
    	return this;
    }

    /**exp: jwt的过期时间，这个过期时间必须要大于签发时间*/
    public BuilderUtil setTokenPayloadExpiresAt(final Date expiresAt) 
    {
    	this.builder.withExpiresAt(expiresAt);
    	return this;
    }

    /**定义在什么时间之前，该jwt都是不可用的.*/
    public BuilderUtil setTokenPayloadNotBefore(final Date notBefore) 
    {
    	this.builder.withNotBefore(notBefore);
    	return this;
    }

    /**jwt的签发时间*/
    public BuilderUtil setTokenPayloadIssuedAt(final Date issuedAt) 
    {
    	this.builder.withIssuedAt(issuedAt);
    	return this;
    }

    /** jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。*/
    public BuilderUtil setTokenPayloadJWTId(final String jwtId) 
    {
    	this.builder.withJWTId(jwtId);
    	return this;
    }
    
    private static class BuilderUtilException extends Exception
    {
		private static final long serialVersionUID = 1679883940952280151L;

		private BuilderUtilException(final String message)
    	{ super(message); }
    }
}