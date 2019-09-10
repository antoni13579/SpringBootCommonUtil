package com.CommonUtils.Utils.SystemUtils.RemoteUtils.Ssh;

import com.CommonUtils.Utils.DataTypeUtils.ArrayUtils.ArrayUtil;
import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

public final class SSHCommonCommand 
{
	private SSHCommonCommand() {}
	
	private static final String FIND_COMMAND = "find param0 -name param1";
	private static final String ZCAT_AND_WC_COMMAND = "zcat param0 | wc -l";
	private static final String CAT_AND_WC_COMMAND = "cat param0 | wc -l";
	private static final String PERL_COMMAND = "perl filePath paramN";
	private static final String PS_EF_COMMAND = "ps -ef | grep -v grep";
	private static final String MV_COMMAND = "mv -f param0 param1";
	
	public static String getMvCommand(final String ... args)
	{
		if (null != args && args.length == 2)
		{
			String result = MV_COMMAND;
			for (int i = 0; i < args.length; i++)
			{
				if (!StringUtil.isStrEmpty(args[i]))
				{ result = result.replaceAll("param" + i, args[i]); }
			}
			
			return result;
		}
		
		return null;
	}
	
	public static String getPsEfCommand(final String ... pipes)
	{
		if (!ArrayUtil.isArrayEmpty(pipes))
		{
			StringBuilder result = new StringBuilder();
			result.append(PS_EF_COMMAND);
			result.append(" | ");
			for (int i = 0; i < pipes.length; i++)
			{
				if (!StringUtil.isStrEmpty(pipes[i]))
				{
					result.append(pipes[i]);
					
					if (i != pipes.length - 1)
					{ result.append(" | "); }
				}
			}
			return result.toString();
		}
		
		return PS_EF_COMMAND;
	}
	
	public static String getPerlCommand(final String filePath, final String ... args)
	{
		if (!StringUtil.isStrEmpty(filePath))
		{
			String result = PERL_COMMAND.replaceAll("filePath", filePath);
			
			if (!ArrayUtil.isArrayEmpty(args))
			{
				for (int i = 0; i < args.length; i++)
				{
					if (!StringUtil.isStrEmpty(args[i]))
					{
						if (0 == i)
						{ result = result.replaceAll("paramN", args[i]); }
						else
						{
							StringBuilder sb = new StringBuilder();
							sb.append(result).append(" ").append(args[i]);
							result = sb.toString();
						}
					}
				}
			}
			else
			{ result = result.replaceAll(" paramN", ""); }
			
			return result;
		}
		
		return null;
	}
	
	public static String getFindCommand(final String ... args)
	{
		if (null != args && args.length == 2)
		{
			String result = FIND_COMMAND;
			for (int i = 0; i < args.length; i++)
			{
				if (!StringUtil.isStrEmpty(args[i]))
				{ result = result.replaceAll("param" + i, args[i]); }
			}
			
			return result;
		}
		
		return null;
	}
	
	public static String getZcatAndWcCommand(final String arg)
	{
		if (!StringUtil.isStrEmpty(arg))
		{
			String result = ZCAT_AND_WC_COMMAND;
			result = result.replaceAll("param0", arg);
			return result;
		}
		
		return null;
	}
	
	public static String getCatAndWcCommand(final String arg)
	{
		if (!StringUtil.isStrEmpty(arg))
		{
			String result = CAT_AND_WC_COMMAND;
			result = result.replaceAll("param0", arg);
			return result;
		}
		
		return null;
	}
}