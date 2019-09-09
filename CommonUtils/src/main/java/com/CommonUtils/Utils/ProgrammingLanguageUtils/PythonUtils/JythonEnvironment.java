package com.CommonUtils.Utils.ProgrammingLanguageUtils.PythonUtils;

import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public final class JythonEnvironment 
{
	private static JythonEnvironment INSTANCE = null;
	
	private JythonEnvironment()  
	{}
	
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