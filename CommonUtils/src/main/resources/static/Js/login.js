//加载这个js，自动调用startUp()函数
startUp();

//初始化页面，login.html程序主入口
function startUp()
{
	//注入javascript（自定义框架）
	importScript("../Js/Utils/StringUtils/StringContants.js", "body");		//使用了PATTERN_3常量
	importScript("../Js/Utils/StringUtils/StringUtil.js", "body");			//使用了isStrEmpty与validateRegExp方法
	importScript("../Js/Utils/AjaxUtils/Jquery/JqueryAjaxUtil.js", "body"); //使用了commonAjaxForJqueryByPost方法
	
	//注入css（自定义文件）
	importStyle("../Css/login.css", "body");
	
	//初始化控件
	$(document).ready
	(
			function()
 			{
				//登录界面，登录按钮点击后，就调用登录模块
				$("#submitBtn").click(function() { login(); });
 				
 				//登录界面，清空按钮点击后，就把所有信息重置
 				$("#clearBtn").click(function() { clearLoginInfo(); });
 				
 				//登录界面，点击了【还没有账号？点我注册】，则弹出注册窗口
 				$("#registerLink").click(function() { $('#registerDiv').modal('show'); });
				
				//注册界面，提交注册信息按钮点击后，就调用注册模块
				$("#registerBtn").click(function() { register(); });
				
				//注册界面被关闭后，需要进行重置动作
				$("#registerDiv").on
				(
						"hidden.bs.modal",
						function(e)
						{
							clearRegisterInfo();
							clearLoginInfo();
						}
				);
 			}
	);
}

//登录模块，注册模块操作后，提示框显示信息
function setPromptInfo(elementId, addClassStyle, showText)
{
	var tmpElement = $("#" + elementId).removeClass();
	if (!isStrEmpty(addClassStyle))
	{ tmpElement.addClass(addClassStyle); }
	tmpElement.text(showText);
}

//清空登录form所有信息
function clearLoginInfo()
{
	$("#username").val("");
	$("#password").val("");
	setPromptInfo("responseInfo", null, "");
}

//清空注册form所有信息
function clearRegisterInfo()
{
	$("#usr").val("");
	$("#pwd").val("");
	$("#reTypePwd").val("");
	$("#eMail").val("");
	setPromptInfo("responseRegisterInfo", null, "");
}

//注册请求模块
function register()
{
	var usrName = $("#usr").val();
	var passWord = $("#pwd").val();
	var reTypePassWord = $("#reTypePwd").val();
	var eMail = $("#eMail").val();
	
	//所有字段不为空，才能进行下一步操作
	if (!isStrEmpty(usrName) && 
		!isStrEmpty(passWord) && 
		!isStrEmpty(reTypePassWord) && 
		!isStrEmpty(eMail))
	{
		//两次输入的密码不一样
		if (passWord != reTypePassWord) { setPromptInfo("responseRegisterInfo", "alert alert-danger", "两次输入的密码不一致"); }
		//邮箱格式填写不正确
		else if (!validateRegExp(eMail, PATTERN_3)) { setPromptInfo("responseRegisterInfo", "alert alert-danger", "邮箱格式填写不正确"); }
		
		//条件满足，可以提交注册请求
		else
		{
			commonAjaxForJqueryByPost
			(
					"../homeRestController/register",
					{ "registerName" : usrName, "registerPassword" : passWord, "registerEmail" : eMail },
					true,
					"json",
					
					//Ajax成功的处理步骤
					function(data)
					{
						var status = data.status;
						var statusDesc = data.statusDesc;
						
						//状态码与状态信息不为空时，需要进行的处理
						if (!isStrEmpty(status) && !isStrEmpty(statusDesc))
						{
							//注册成功
							if (status == "200")
							{
								setPromptInfo("responseRegisterInfo", "alert alert-success", statusDesc);
								$('#registerDiv').modal('hide');
								clearLoginInfo();
							}
							//注册失败
							else if (status == "400") { setPromptInfo("responseRegisterInfo", "alert alert-warning", statusDesc); }
							//出现了新的状态码，无法处理，只能显示在页面
							else { setPromptInfo("responseRegisterInfo", "alert alert-danger", "服务端返回的信息出现新状态，无法进行处理"); }
						}
						
						//否则返回状态为空给页面
						else { setPromptInfo("responseRegisterInfo", "alert alert-danger", "服务端返回的信息为空，无法进行处理"); }
					},
					
					//Ajax失败的处理步骤
					function(data) { setPromptInfo("responseRegisterInfo", "alert alert-danger", "Ajax请求失败"); }
			);
		}
	}
	
	//若为空，则提示信息给页面
	else { setPromptInfo("responseRegisterInfo", "alert alert-danger", "所有字段不能为空"); }
}
    	
//登录请求模块
function login()
{
	var account = $("#username").val();
	var pwd = $("#password").val();
	if (!isStrEmpty(account) && !isStrEmpty(pwd))
	{		
		commonAjaxForJqueryByPost
		(
				"../homeController/login", 
				{ "username" : account, "password" : pwd }, 
				true,
				"json",
				
				//Ajax成功的处理步骤
				function(data) 
				{
					var status = data.status;
					var statusDesc = data.statusDesc;
					
					//状态码与状态信息不为空时，需要进行的处理
					if (!isStrEmpty(status) && !isStrEmpty(statusDesc))
					{
						//登录成功
						if (status == "200")
						{
							setPromptInfo("responseInfo", "alert alert-success", statusDesc);
							window.location.href="../"; 
						}
						//登录失败
						else if (status == "401") { setPromptInfo("responseInfo", "alert alert-warning", statusDesc); }
						//出现了新的状态码，无法处理，只能显示在页面
						else { setPromptInfo("responseInfo", "alert alert-danger", "服务端返回的信息出现新状态，无法进行处理"); }
					}
					
					//否则返回状态信息为空给页面
					else { setPromptInfo("responseInfo", "alert alert-danger", "服务端返回的信息为空，无法进行处理"); }
				},
				
				//Ajax失败的处理步骤
				function(data) { setPromptInfo("responseInfo", "alert alert-danger", "Ajax请求失败"); }
		);
	}
	else
	{ setPromptInfo("responseInfo", "alert alert-danger", "用户名与密码不能为空"); }
}