package com.CommonUtils.Utils.FrameworkUtils.JwtUtils.auth0Jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

public final class JwtUtil 
{
	private JwtUtil() {}
	
	public static DecodedJWT verify(final JWTVerifier jwtVerifier, 
									final String token)
	{ return jwtVerifier.verify(token); }
	
	public static DecodedJWT getDecodedJWT(final String token)
	{ return JWT.decode(token); }
}