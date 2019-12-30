package com.CommonUtils.Config.Security.Shiro.Config;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.CommonUtils.Config.Security.Shiro.Bean.RealmAndCredentialsMatcherDefinition;
import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

import cn.hutool.core.collection.CollUtil;

public final class SecurityManagerConfig 
{
	private SecurityManagerConfig() {}
	
	public static DefaultWebSecurityManager getInstance(final boolean closeSession, final RealmAndCredentialsMatcherDefinition ... realmAndCredentialsMatcherDefinitions) throws Exception
	{
		if (cn.hutool.core.util.ArrayUtil.isEmpty(realmAndCredentialsMatcherDefinitions))
		{ throw new Exception("必须设置用户鉴权逻辑！！"); }
		
		Collection<Realm> realms = new ArrayList<>();		
		JavaCollectionsUtil.collectionProcessor
		(
				CollUtil.newArrayList(realmAndCredentialsMatcherDefinitions), 
				(final RealmAndCredentialsMatcherDefinition value, final int indx, final int length) -> 
				{ realms.add(value.getRealm()); }
		);
		
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealms(realms);
		
		if (closeSession)
		{
			/*
			 * 关闭shiro自带的session，详情见文档
			 * http://shiro.apache.org/session-management.html#SessionManagement-
			 * StatelessApplications%28Sessionless%29
			 */
			DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
			defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
			DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
			subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
			securityManager.setSubjectDAO(subjectDAO);
		}
		
		return securityManager;
	}
}