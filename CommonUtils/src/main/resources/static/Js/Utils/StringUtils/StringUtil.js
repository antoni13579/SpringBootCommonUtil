/**
 * 判断字符串是否为空
 * @param str 需要判断的字符串
 * @return 返回布尔值，true为空，false为非空
 * */
function isStrEmpty(str)
{
	//跟undefined比对不需要typeof
	if (str === "undefined" || str == null || str == "")
	{ return true; }
	else
	{ return false; }
};

/**
 * 验证输入的字符串，是否符合正则表达式
 * @param str 需要判断的字符串；
 * 		  pattern 正则表达式
 * @return 返回布尔值，true为验证通过，false为验证不通过
 * */
function validateRegExp(str, pattern)
{
	var reg = new RegExp(pattern);
	if (reg.test(str))
	{ return true; }
	else
	{ return false; }
};