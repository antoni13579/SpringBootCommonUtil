/**
 * 注入javaScript文件，jsFileSrc是js文件路径，importHtmlElementLocation是指导入到那个Html元素，可以输入head或body，一般只是用这个两个
 * */
function importScript(jsFileSrc, importHtmlElementLocation)
{
	var script = document.createElement("script");
	script.language = "javascript";
	script.src = jsFileSrc;
	document.getElementsByTagName(importHtmlElementLocation)[0].appendChild(script);
}

/**
 * 注入css文件，cssFileSrc是css文件路径，importHtmlElementLocation是指导入到那个Html元素，可以输入head或body，一般只是用这个两个
 * */
function importStyle(cssFileSrc, importHtmlElementLocation)
{
	var cssStyle = document.createElement("link");
	cssStyle.rel = "stylesheet";
	cssStyle.href = cssFileSrc;
	document.getElementsByTagName(importHtmlElementLocation)[0].appendChild(cssStyle);
}

/**
 * 禁止页面后退
 * */
function NoBackOffForPage()
{
	if (window.history && window.history.pushState) 
	{
        $(window).on
        (
        		'popstate', 
        		function () 
        	    {
        			window.history.pushState('forward', null, '#');
                    window.history.forward(1);
                }
        );
    }
	
    if ('pushState' in history) 
    {
    	window.history.pushState('forward', null, '#');
        window.history.forward(1);                    
    }
    else
    {
    	History.pushState('forward', null, '?state=2');
        window.history.forward(1);
    }
    
    window.onhashchange = function() { History.pushState('forward', null, '?state=1'); }
}