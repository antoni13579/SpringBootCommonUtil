package com.CommonUtils.Shiro.Bean;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthorizingRealm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString
public final class RealmAndCredentialsMatcherDefinition 
{
	private AuthorizingRealm realm;
	private CredentialsMatcher credentialsMatcher;
	
	public RealmAndCredentialsMatcherDefinition configCredentialsMatcher()
	{
		if (null != this.credentialsMatcher)
		{ this.realm.setCredentialsMatcher(this.credentialsMatcher); }
		
		return this;
	}
}