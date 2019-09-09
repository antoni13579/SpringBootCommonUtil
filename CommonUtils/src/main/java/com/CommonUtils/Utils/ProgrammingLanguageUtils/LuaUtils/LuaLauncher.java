package com.CommonUtils.Utils.ProgrammingLanguageUtils.LuaUtils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LuaLauncher 
{
	private Globals globals;
	
	public LuaLauncher(String luaScript) 
	{
		this.globals = JsePlatform.standardGlobals();
		this.globals.load(luaScript).call();
	}
	
	public void invoke(final String func, final Object ... parameters)
	{		
		if (null == parameters || parameters.length == 0)
		{ this.globals.get(func).call(); }
		else if (parameters.length <= 3)
		{
			Object parameter1 = parameters.length >= 1 ? parameters[0] : null;
			Object parameter2  = parameters.length >= 2 ? parameters[1] : null;
			Object parameter3 = parameters.length >= 3 ? parameters[2] : null;
			this.globals.get(func).call(toLua(parameter1), toLua(parameter2), toLua(parameter3));
		}
		else
		{ log.warn("调用Lua脚本，参数值大于3个，请及时处理，lua脚本={}，参数={}", func, parameters); }
	}
	
	/**
	 * Java对象转换为Lua对象
	 */
	private LuaValue toLua(final Object javaValue)
	{
		if (null == javaValue)
		{ return LuaValue.NIL; }
		else
		{
			if (javaValue instanceof LuaValue)
			{ return (LuaValue) javaValue; }
			else
			{ return CoerceJavaToLua.coerce(javaValue); }
		}
	}
}