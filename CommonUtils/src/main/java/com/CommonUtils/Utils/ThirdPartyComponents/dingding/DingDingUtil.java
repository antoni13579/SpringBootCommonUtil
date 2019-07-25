package com.CommonUtils.Utils.ThirdPartyComponents.dingding;

import java.util.List;
import java.util.Optional;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiSsoGettokenRequest;
import com.dingtalk.api.request.OapiSsoGetuserinfoRequest;
import com.dingtalk.api.request.OapiUserGetDeptMemberRequest;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.request.OapiUserSimplelistRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiSsoGettokenResponse;
import com.dingtalk.api.response.OapiSsoGetuserinfoResponse;
import com.dingtalk.api.response.OapiUserGetDeptMemberResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.dingtalk.api.response.OapiUserSimplelistResponse;
import com.dingtalk.api.response.OapiUserSimplelistResponse.Userlist;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DingDingUtil 
{
	private DingDingUtil() {}
	
	/*** 获取钉钉的Access_Token
	 * @param appkey 应用的唯一标识key
	 * @param appsecret 应用的密钥
	 * */
	public static Optional<OapiGettokenResponse> getAccessTokenBean(final String appkey, final String appsecret)
	{
		DefaultDingTalkClient client = new DefaultDingTalkClient(Contants.GET_TOKEN_URL);
		OapiGettokenRequest request = new OapiGettokenRequest();
		request.setAppkey(appkey);
		request.setAppsecret(appsecret);
		request.setHttpMethod("GET");
		
		OapiGettokenResponse response = null;
		try
		{ response = client.execute(request); }
		catch (Exception ex)
		{ log.error("获取钉钉accessToken出错，异常原因为：", ex); }
		
		return Optional.ofNullable(response);
	}
	
	/*** 获取钉钉的Access_Token
	 * @param appkey 应用的唯一标识key
	 * @param appsecret 应用的密钥
	 * */
	public static String getAccessToken(final String appkey, final String appsecret) throws Exception
	{
		return getAccessTokenBean(appkey, appsecret)
					.orElseThrow(() -> new Exception("钉钉accessToken为空！！"))
					.getAccessToken();
	}
	
	
	/*** 获取钉钉SSO的Access_Token（用于应用管理后台免登）
	 * @param corpId 企业Id
	 * @param corpSecret 这里必须填写专属的SSOSecret
	 * */
	public static Optional<OapiSsoGettokenResponse> getSsoAccessTokenBean(final String corpId, final String corpSecret)
	{
		DingTalkClient client = new DefaultDingTalkClient(Contants.GET_SSO_TOKEN_URL);
		OapiSsoGettokenRequest request = new OapiSsoGettokenRequest();
		request.setCorpid(corpId);
		request.setCorpsecret(corpSecret);
		request.setHttpMethod("GET");
		
		OapiSsoGettokenResponse response = null;
		try
		{ response = client.execute(request); }
		catch (Exception ex)
		{ log.error("获取钉钉SSO的accessToken出错，异常原因为：", ex); }
		
		return Optional.ofNullable(response);
	}
	
	/*** 获取钉钉SSO的Access_Token（用于应用管理后台免登）
	 * @param corpId 企业Id
	 * @param corpSecret 这里必须填写专属的SSOSecret
	 * */
	public static String getSsoAccessToken(final String corpId, final String corpSecret) throws Exception
	{
		return getSsoAccessTokenBean(corpId, corpSecret)
					.orElseThrow(() -> new Exception("钉钉SSO的accessToken为空！！"))
					.getAccessToken();
	}
	
	/**
	 * 获取钉钉用户信息（用于企业内部应用免登）
	 * @param requestAuthCode 免登授权码
	 * @param accessToken 调用接口凭证
	 * */
	public static Optional<OapiUserGetuserinfoResponse> getUserInfoBean(final String requestAuthCode, final String accessToken)
	{
		DingTalkClient client = new DefaultDingTalkClient(Contants.GET_USER_INFO_URL);
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(requestAuthCode);
		request.setHttpMethod("GET");
		
		OapiUserGetuserinfoResponse response = null;
		try
		{ response = client.execute(request, accessToken); }
		catch (Exception ex)
		{ log.error("获取钉钉用户信息出错，异常原因为：", ex); }
		
		return Optional.ofNullable(response);
	}
	
	/**
	 * 获取钉钉用户信息（用于企业内部应用免登）
	 * @param requestAuthCode 免登授权码
	 * @param accessToken 调用接口凭证
	 * */
	public static String getUserInfo(final String requestAuthCode, final String accessToken) throws Exception
	{
		return getUserInfoBean(requestAuthCode, accessToken)
					.orElseThrow(() -> new Exception("钉钉用户信息为空！！"))
					.getUserid();
	}
	
	/**
	 * 获取钉钉SSO用户信息（用于应用管理后台免登）
	 * @param code 通过Oauth认证给URL带上的CODE
	 * @param ssoAccessToken 再次强调，此token不同于一般的accessToken，需要调用获取微应用管理员免登需要的AccessToken
	 * */
	public static Optional<OapiSsoGetuserinfoResponse> getSsoUserInfoBean(final String code, final String ssoAccessToken)
	{
		DingTalkClient client = new DefaultDingTalkClient(Contants.GET_SSO_USER_INFO_URL);
		OapiSsoGetuserinfoRequest request = new OapiSsoGetuserinfoRequest();
		request.setCode(code);
		request.setHttpMethod("GET");
		
		OapiSsoGetuserinfoResponse response = null;
		try
		{ response = client.execute(request, ssoAccessToken); }
		catch (Exception ex)
		{ log.error("获取钉钉SSO用户信息出错，异常原因为：", ex); }
		
		return Optional.ofNullable(response);
	}
	
	/**
	 * 获取钉钉用户详情
	 * 
	 * */
	public static Optional<OapiUserGetResponse> getUserDetailBean(final String userid, final String accessToken)
	{
		DingTalkClient client = new DefaultDingTalkClient(Contants.GET_USER_DETAIL_URL);
		OapiUserGetRequest request = new OapiUserGetRequest();
		request.setUserid(userid);
		request.setHttpMethod("GET");
		
		OapiUserGetResponse response = null;
		try
		{ response = client.execute(request, accessToken); }
		catch (Exception ex)
		{ log.error("获取钉钉用户详情出错，异常原因为：", ex); }
		
		return Optional.ofNullable(response);
	}
	
	/**
	 * 获取钉钉部门用户userid列表
	 * @param deptId 部门id
	 * @param accessToken 调用接口凭证
	 * */
	public static Optional<OapiUserGetDeptMemberResponse> getDeptMemberBean(final String deptId, final String accessToken)
	{
		DingTalkClient client = new DefaultDingTalkClient(Contants.GET_DEPT_MEMBER_URL);
		OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
		req.setDeptId(deptId);
		req.setHttpMethod("GET");
		
		OapiUserGetDeptMemberResponse rsp = null;
		try
		{ rsp = client.execute(req, accessToken); }
		catch (Exception ex)
		{ log.error("获取钉钉部门用户userid列表出错，异常原因为：", ex); }
		
		return Optional.ofNullable(rsp);
	}
	
	/**
	 * 获取钉钉部门用户
	 * @param deptId 获取的部门id
	 * @param offset 支持分页查询，与size参数同时设置时才生效，此参数代表偏移量
	 * @param size 支持分页查询，与offset参数同时设置时才生效，此参数代表分页大小，最大100
	 * @param accessToken 调用接口凭证
	 * */
	public static Optional<OapiUserSimplelistResponse> getDeptMemberBean(final long deptId, final long offset, final long size, final String accessToken)
	{
		DingTalkClient client = new DefaultDingTalkClient(Contants.GET_DEPT_MEMBER_SIMPLE_LIST_URL);
		OapiUserSimplelistRequest request = new OapiUserSimplelistRequest();
		request.setDepartmentId(deptId);
		request.setOffset(offset);
		request.setSize(size);
		request.setHttpMethod("GET");

		OapiUserSimplelistResponse response = null;
		try
		{ response = client.execute(request, accessToken); }
		catch (Exception ex)
		{ log.error("获取钉钉部门用户出现异常，异常原因为：", ex); }
		
		return Optional.ofNullable(response);
	}
	
	/**
	 * 获取钉钉部门用户userid列表
	 * @param deptId 部门id
	 * @param accessToken 调用接口凭证
	 * */
	public static List<String> getDeptMember(final String deptId, final String accessToken) throws Exception
	{
		return getDeptMemberBean(deptId, accessToken)
					.orElseThrow(() -> new Exception("钉钉部门用户userid列表为空！！"))
					.getUserIds();
	}
	
	/**
	 * 获取钉钉部门用户
	 * @param deptId 获取的部门id
	 * @param offset 支持分页查询，与size参数同时设置时才生效，此参数代表偏移量
	 * @param size 支持分页查询，与offset参数同时设置时才生效，此参数代表分页大小，最大100
	 * @param accessToken 调用接口凭证
	 * */
	public static List<Userlist> getDeptMember(final long deptId, final long offset, final long size, final String accessToken) throws Exception
	{
		return getDeptMemberBean(deptId, offset, size, accessToken)
					.orElseThrow(() -> new Exception("钉钉部门用户为空！！"))
					.getUserlist();
	}
}