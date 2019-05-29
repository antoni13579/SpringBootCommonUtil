//加载这个js，自动调用startUp()函数
startUp();

//index.html程序主入口
function startUp()
{		
	//使用Jquery等待页面加载完成后，才执行下面的代码
	$(document).ready( function() { init(); } );
}

//初始化整个主页
function init()
{
	/*启动angular模块*/
	angular.element().ready(function () { angular.bootstrap(document, ['commonUtilsApp']); });
	
	//正式开始任务
	var commonUtilsApp = angular
	
		/**由于是单页应用，首次运行需要注册commonUtilsApp，在index.html已经定义了，定义在ng-app，不注册就会报错
		 * ngRoute为angular路由配置，不用说，一定要配置，js文件为angular-route.js
		 * oc.lazyLoad为ocLazyLoad懒加载配置，也一定要配置，js文件为ocLazyLoad.min.js
		 * */
		.module('commonUtilsApp', [ 'ngRoute', 'oc.lazyLoad' ])
		
		//这里是配置get，post请求默认使用application/x-www-form-urlencoded
		.config
		(
				function($httpProvider)
				{
					$httpProvider.defaults.headers.put['Content-Type'] = 'application/x-www-form-urlencoded';
					$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
				}
		)
		
		//配置控制器，一般是用于呈现页面菜单之类的
		.controller
		(
				'mainController',
				[
					'$scope',
					'$http',
					'$compile', 
					'$ocLazyLoad',
					function($scope, $http, $compile, $ocLazyLoad)
					{
						/*获取用户菜单模组，显示效果在header.tpl.html体现出来*/
						$scope.userModuleList = getUserModule();
						
						/*实现注销功能，显示效果在header.tpl.html体现出来*/
						$scope.logout = function () { window.location.href="../logout"; };
						
						/**切换用户菜单模组，显示效果在header.tpl.html体现出来*/
						$scope.switchModule = function (menuType) 
						{
							$scope.menusOfModule = getMenusOfModule(menuType);
							sdf();
						};
						
						/**获取当前登录的用户名*/
						$scope.userName = getCurrentUserName();
						
						/*多级菜单伸缩控制*/
						$scope.toggleExpandNextLevel = function($event) { toggleExpandNextLevel($event); }
						
						/**当点击【修改密码】，弹出修改界面*/
						$scope.modifyPassWord = function() { $('#modifyPasswordDiv').modal('show'); }
						
						$scope.openWebSocketTest = function() { window.open("../homeController/webSocketPage"); }
						
						 /**监听ng-repeat中ngRepeatFinished事件，意思是ng-repeat已完成任务，需要做下一步动作*/
						$scope.$on
					    (
					    		"ngRepeatFinished", 
				        		function (repeatFinishedEvent, element)
				        		{
					    			for (var externalIndex = 0; externalIndex < element.parent().length; externalIndex++)
				        			{
				        				var elementWithEvent = $(element.parent()[externalIndex]);
				        				for (var internalIndex = 0; internalIndex < elementWithEvent.length; internalIndex++)
				        				{
				        					//在navigate.tpl.html中，只有二级菜单才能被隐藏
				        					if (elementWithEvent[internalIndex].id === 'secondMenu')
				        					{ elementWithEvent.hide(0); }
				        				}
				        			}
				        		} 
					    );
					}
				]
		)
		
		//自定义标签，用于处理ng-repeat渲染完毕后的后续事情，标签名字为on-repeat-finished-render，目前使用在navigate.tpl.html中
		.directive
		(
				'onRepeatFinishedRender', 
				[
					'$timeout', 
					function ($timeout) 
					{
						//这个大括号不能格式化，格式化会导致报错
						return {
					        restrict: 'A',
					        link: function(scope, element, attr) 
					        {
					            if (scope.$last === true) 
					            {
					            	$timeout
					            	(
					            			function()
					            			{
					            				//这里element, 就是ng-repeat渲染的最后一个元素
					            		         scope.$emit('ngRepeatFinished', element);
					            			}
					            	);
					            }  
					        }  
					    }; 
					}
				]
		);
}

function sdf()
{
	console.log(document.querySelector("#mainNavigation"));
}

/**多级菜单伸缩控制*/
function toggleExpandNextLevel(event)
{
	var elementWithEvent = event.srcElement ? event.srcElement : event.target;
    var $elementWithEvent = $(elementWithEvent);
    
    //非点击的节点全部收缩
    $elementWithEvent.parents().siblings().find(".second").hide(200);
    
    //点击的节点全部展开
    $elementWithEvent.siblings(".second").toggle(200);
}

/*获取模组下的全部子级菜单*/
function getMenusOfModule (menuType)
{
	var menusOfModule = new Array();
	
	commonAjaxForJqueryByGet
	(
			"../homeRestController/getMenusOfModule",
			{ "menuType" : menuType },
			false,
			"json",
			
			//Ajax成功的处理步骤
			function(data)
			{
				for (var i = 1; i < data.length; i++)
				{ menusOfModule[i - 1] = data[i]; }
			},
			
			//Ajax失败的处理步骤
			function(data) { alert("获取用户菜单模组下全部子级菜单失败！！！，请及时找管理员协查。。"); }
	);
	
	return menusOfModule;
}

/*获取用户菜单模组
 * 
 * 默认情况下，JQuery的请求是异步的，不过直接异步会导致userModuleList过早返回，导致返回值为空数组
 * 所以需要把JQuery的Ajax请求设置为同步，确保userModuleList是可以得到返回值
 * */
function getUserModule()
{
	var userModuleList = new Array();
	
	commonAjaxForJqueryByGet
	(
			"../homeRestController/getUserModule",
			null,
			false,
			"json",
			
			//Ajax成功的处理步骤
			function(data)
			{
				if (data.length == 1)
				{
					var userModuleData = data[0];
					for (var i = 1; i < userModuleData.rows.length; i++)
					{ userModuleList[i - 1] = userModuleData.rows[i]; }
				}
				else
				{ alert("用户菜单模组存在多个，请及时找管理员协查"); }
			},
			
			//Ajax失败的处理步骤
			function(data) { alert("获取用户菜单模组失败！！！，请及时找管理员协查。。"); }
	);
	
	return userModuleList;
}

/**获取当前登录的用户名*/
function getCurrentUserName()
{
	var userName = null;
	
	commonAjaxForJqueryByPost
	(
			"../homeRestController/getCurrentUserName",
			null,
			false,
			"json",
			
			//Ajax成功的处理步骤
			function(data)
			{
				userName = data.username;
				if ("defaultAdmin" == userName)
				{ alert("获取到默认用户，存在问题，请及时联系管理员协查。。"); }
			},
			
			//Ajax失败的处理步骤
			function(data) { alert("获取当前用户失败！！！，请及时找管理员协查。。"); }
	);
	
	return userName;
}