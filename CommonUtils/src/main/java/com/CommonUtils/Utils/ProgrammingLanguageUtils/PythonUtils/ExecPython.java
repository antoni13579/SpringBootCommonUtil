package com.CommonUtils.Utils.ProgrammingLanguageUtils.PythonUtils;

import java.util.Map;

import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ExecPython 
{
	INSTANCE;
	
	public void executeFile(final String scriptFilePath, final Map<String, Object> properties)
	{
		try
		{
			//获取python解释器  
	        PythonInterpreter inter = JythonEnvironment.getInstance().getPythonInterpreter();  
	         
	        if (null != properties && properties.isEmpty() == false)
	        {
	        	//设置python属性,python脚本中可以使用  
	            for (Map.Entry<String,Object> entry : properties.entrySet())  
	            { inter.set(entry.getKey(), entry.getValue()); }  
	        }
	          
	        inter.execfile(scriptFilePath);
		}
		catch (Exception e)
		{ log.error("指定Python文件路径并执行脚本出现异常，异常原因为：{}", e); }
	}
	
	public void execute(final String scriptInfo, final Map<String, Object> properties)
	{
		try
		{
			//获取python解释器  
	        PythonInterpreter inter = JythonEnvironment.getInstance().getPythonInterpreter(); 
	        
	        if (null != properties && properties.isEmpty() == false)
	        {
	        	//设置python属性,python脚本中可以使用  
	            for (Map.Entry<String,Object> entry : properties.entrySet())  
	            { inter.set(entry.getKey(), entry.getValue()); }
	        }
	        
	        inter.exec(scriptInfo);
		}
		catch (Exception e)
		{ log.error("指定Python脚本内容并执行脚本出现异常，异常原因为：{}", e); }
	}
	
	private static final class JythonEnvironment 
	{
		private static JythonEnvironment INSTANCE = null;
		
		private JythonEnvironment() {}
		
		public static JythonEnvironment getInstance()
		{
			if (null == INSTANCE)
			{
				synchronized(JythonEnvironment.class)
				{
					if (null == INSTANCE)
					{ INSTANCE = new JythonEnvironment(); }
				}
			}
			return INSTANCE;
		}
		
		/** 
		 * * 获取python系统状态,可根据需要指定classloader/sys.stdin/sys.stdout等 
	     * @return PySystemState 
	     */ 
		private PySystemState getPySystemState()  
	    {  
	        PySystemState.initialize();
	        final PySystemState py = new PySystemState();  
	        py.setClassLoader(getClass().getClassLoader());  
	        return py;  
	    } 
		
		/** 
		 *  * 获取python解释器 
	     * @return PythonInterpreter 
	     */  
	    public PythonInterpreter getPythonInterpreter()  
	    { return new PythonInterpreter(null, getPySystemState()); } 
	}
}