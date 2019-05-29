package com.CommonUtils.SpringSecurity.Config.Role;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

public final class RoleHierarchyConfig 
{
	private RoleHierarchyConfig() {}
	
	public static RoleHierarchy getInstance(final String roleHierarchyStringRepresentation)
	{
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy(roleHierarchyStringRepresentation);
		return roleHierarchy;
	}
}