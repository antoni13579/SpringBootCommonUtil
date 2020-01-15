package com.CommonUtils.Utils.SystemUtils.SystemInfoUtils;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import lombok.Getter;
import lombok.ToString;

/**请使用cn.hutool.system.OsInfo
 * @deprecated
 * */
@Deprecated(since="使用cn.hutool.system.OsInfo")
public final class SystemInfo 
{
	private static String os = null;  
    
    private static SystemInfo instance = new SystemInfo();  
      
    private SystemPlatform platform;  
      
    private SystemInfo(){}  
    
    static
    {
    	String osInfo = System.getProperty("os.name");
    	if (StringUtil.isStrEmpty(osInfo))
    	{ os = ""; }
    	else if (null != osInfo)
    	{ os = osInfo.toLowerCase(); }
    	else
    	{ os = ""; }
    }
      
    public static boolean isLinux() { return os.indexOf("linux") >= 0; }  
    public static boolean isMacOS() { return os.indexOf("mac") >= 0 && os.indexOf("os") > 0 && os.indexOf('x') < 0; } 
    public static boolean isMacOSX(){ return os.indexOf("mac") >= 0 && os.indexOf("os") > 0 && os.indexOf('x') > 0; }  
    public static boolean isWindows() { return os.indexOf("windows") >= 0; }  
    public static boolean isOS2(){ return os.indexOf("os/2") >= 0; }  
    public static boolean isSolaris() { return os.indexOf("solaris") >= 0; }  
    public static boolean isSunOS() { return os.indexOf("sunos") >= 0; }  
    public static boolean isMPEiX() { return os.indexOf("mpe/ix") >= 0; }  
    public static boolean isHPUX() { return os.indexOf("hp-ux") >= 0; }  
    public static boolean isAix() { return os.indexOf("aix") >= 0; }  
    public static boolean isOS390() { return os.indexOf("os/390") >= 0; }  
    public static boolean isFreeBSD() { return os.indexOf("freebsd") >= 0; }  
    public static boolean isIrix() { return os.indexOf("irix") >= 0; }  
    public static boolean isDigitalUnix() { return os.indexOf("digital") >= 0 && os.indexOf("unix") > 0; }  
    public static boolean isNetWare() { return os.indexOf("netware") >= 0; }  
    public static boolean isOSF1() { return os.indexOf("osf1") >= 0; }  
    public static boolean isOpenVMS() { return os.indexOf("openvms") >= 0; }  
      
    /** 
     * 获取操作系统名字 
     * @return 操作系统名 
     */  
    public static SystemPlatform getOSname()
    {  
        if(isAix()) { instance.platform = SystemPlatform.AIX; }
        else if (isDigitalUnix()) { instance.platform = SystemPlatform.DIGITAL_UNIX; }
        else if (isFreeBSD()) { instance.platform = SystemPlatform.FREE_BSD ; }
        else if (isHPUX()) { instance.platform = SystemPlatform.HP_UX; }
        else if (isIrix()) { instance.platform = SystemPlatform.IRIX; }
        else if (isLinux()) { instance.platform = SystemPlatform.LINUX; }
        else if (isMacOS()) { instance.platform = SystemPlatform.MAC_OS; }
        else if (isMacOSX()) { instance.platform = SystemPlatform.MAC_OS_X; }
        else if (isMPEiX()) { instance.platform = SystemPlatform.MPEIX; }
        else if (isNetWare()) { instance.platform = SystemPlatform.NETWARE_411; }
        else if (isOpenVMS()) { instance.platform = SystemPlatform.OPEN_VMS; }
        else if (isOS2()) { instance.platform = SystemPlatform.OS2; }
        else if (isOS390()) { instance.platform = SystemPlatform.OS390; }
        else if (isOSF1()) { instance.platform = SystemPlatform.OSF1; }
        else if (isSolaris()) { instance.platform = SystemPlatform.SOLARIS; }
        else if (isSunOS()) { instance.platform = SystemPlatform.SUNOS; }
        else if (isWindows()) { instance.platform = SystemPlatform.WINDOWS; }
        else { instance.platform = SystemPlatform.OTHERS; }  
        
        return instance.platform;  
    }
    
    @ToString
    @Getter
    public enum SystemPlatform 
    {
    	ANY("any"),  
        LINUX("Linux"),  
        MAC_OS("Mac OS"),  
        MAC_OS_X("Mac OS X"),  
        WINDOWS("Windows"),  
        OS2("OS/2"),  
        SOLARIS("Solaris"),  
        SUNOS("SunOS"),  
        MPEIX("MPE/iX"),  
        HP_UX("HP-UX"),  
        AIX("AIX"),  
        OS390("OS/390"),  
        FREE_BSD("FreeBSD"),  
        IRIX("Irix"),  
        DIGITAL_UNIX("Digital Unix"),  
        NETWARE_411("NetWare"),  
        OSF1("OSF1"),  
        OPEN_VMS("OpenVMS"),  
        OTHERS("Others");  
          
        private SystemPlatform(final String description)
        { this.description = description; }
          
        private final String description;  
    }
}