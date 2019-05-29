package com.CommonUtils.Utils.SystemInfoUtils;

import com.CommonUtils.Utils.StringUtils.StringUtil;

public final class SystemInfo 
{
	private static String OS = null;  
    
    private static SystemInfo _instance = new SystemInfo();  
      
    private SystemPlatform platform;  
      
    private SystemInfo(){}  
    
    static
    {
    	//本来用StringUtil.isStrEmpty就可以了，不过360代码质量检查老是说我没有做判空，硬是写个else if (null != osInfo)
    	String osInfo = System.getProperty("os.name");
    	if (StringUtil.isStrEmpty(osInfo))
    	{ OS = ""; }
    	else if (null != osInfo)
    	{ OS = osInfo.toLowerCase(); }
    	else
    	{ OS = ""; }
    }
      
    public static boolean isLinux() { return OS.indexOf("linux") >= 0; }  
    public static boolean isMacOS() { return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0; } 
    public static boolean isMacOSX(){ return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0; }  
    public static boolean isWindows() { return OS.indexOf("windows") >= 0; }  
    public static boolean isOS2(){ return OS.indexOf("os/2") >= 0; }  
    public static boolean isSolaris() { return OS.indexOf("solaris") >= 0; }  
    public static boolean isSunOS() { return OS.indexOf("sunos") >= 0; }  
    public static boolean isMPEiX() { return OS.indexOf("mpe/ix") >= 0; }  
    public static boolean isHPUX() { return OS.indexOf("hp-ux") >= 0; }  
    public static boolean isAix() { return OS.indexOf("aix") >= 0; }  
    public static boolean isOS390() { return OS.indexOf("os/390") >= 0; }  
    public static boolean isFreeBSD() { return OS.indexOf("freebsd") >= 0; }  
    public static boolean isIrix() { return OS.indexOf("irix") >= 0; }  
    public static boolean isDigitalUnix() { return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0; }  
    public static boolean isNetWare() { return OS.indexOf("netware") >= 0; }  
    public static boolean isOSF1() { return OS.indexOf("osf1") >= 0; }  
    public static boolean isOpenVMS() { return OS.indexOf("openvms") >= 0; }  
      
    /** 
     * 获取操作系统名字 
     * @return 操作系统名 
     */  
    public static SystemPlatform getOSname()
    {  
        if(isAix()) { _instance.platform = SystemPlatform.AIX; }
        else if (isDigitalUnix()) { _instance.platform = SystemPlatform.Digital_Unix; }
        else if (isFreeBSD()) { _instance.platform = SystemPlatform.FreeBSD; }
        else if (isHPUX()) { _instance.platform = SystemPlatform.HP_UX; }
        else if (isIrix()) { _instance.platform = SystemPlatform.Irix; }
        else if (isLinux()) { _instance.platform = SystemPlatform.Linux; }
        else if (isMacOS()) { _instance.platform = SystemPlatform.Mac_OS; }
        else if (isMacOSX()) { _instance.platform = SystemPlatform.Mac_OS_X; }
        else if (isMPEiX()) { _instance.platform = SystemPlatform.MPEiX; }
        else if (isNetWare()) { _instance.platform = SystemPlatform.NetWare_411; }
        else if (isOpenVMS()) { _instance.platform = SystemPlatform.OpenVMS; }
        else if (isOS2()) { _instance.platform = SystemPlatform.OS2; }
        else if (isOS390()) { _instance.platform = SystemPlatform.OS390; }
        else if (isOSF1()) { _instance.platform = SystemPlatform.OSF1; }
        else if (isSolaris()) { _instance.platform = SystemPlatform.Solaris; }
        else if (isSunOS()) { _instance.platform = SystemPlatform.SunOS; }
        else if (isWindows()) { _instance.platform = SystemPlatform.Windows; }
        else { _instance.platform = SystemPlatform.Others; }  
        
        return _instance.platform;  
    }
}