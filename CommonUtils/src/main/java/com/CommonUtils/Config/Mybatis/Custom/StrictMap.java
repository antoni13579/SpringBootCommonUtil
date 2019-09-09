package com.CommonUtils.Config.Mybatis.Custom;

import java.util.HashMap;
import java.util.Map;

import com.CommonUtils.Utils.CommonUtils.CommonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 重写 org.apache.ibatis.session.Configuration.StrictMap 类 
 * 来自 MyBatis3.4.0版本，修改 put 方法，允许反复 put更新。 
 */
@Slf4j
public final class StrictMap<V> extends HashMap<String, V> 
{
	private static final long serialVersionUID = 1972520261501109344L;
	private String name;

    public StrictMap(final String name, final int initialCapacity, final float loadFactor) 
    {  
        super(initialCapacity, loadFactor);  
        this.name = name;  
    }  

    public StrictMap(final String name, final int initialCapacity) 
    {  
        super(initialCapacity);  
        this.name = name;  
    }  

    public StrictMap(final String name) 
    {  
        super();  
        this.name = name;  
    }  

    public StrictMap(final String name, final Map<String, ? extends V> m) 
    {  
        super(m);  
        this.name = name;  
    }  

    public V put(final String key, final V value) 
    {  
        // ThinkGem 如果现在状态为刷新，则刷新(先删除后添加)  
        if (MapperRefresh.isRefresh()) 
        {  
            remove(key);  
            log.info("MapperRefresh重刷新的key为：{}", key.substring(key.lastIndexOf(".") + 1));
        }  
        
        // ThinkGem end  
        if (containsKey(key)) 
        { throw new IllegalArgumentException(name + "已经包含" + key + "的值"); }
        
        if (key.contains(".")) 
        {  
            final String shortKey = getShortName(key);  
            if (super.get(shortKey) == null) 
            { super.put(shortKey, value); } 
            else 
            { super.put(shortKey, CommonUtil.cast(new Ambiguity(shortKey))); }  
        }  
        return super.put(key, value);  
    }
    
    public StrictMap<V> putForChain(String key, V value)
    {
    	this.put(key, value);
    	return this;
    }

    public V get(final Object key) 
    {  
        V value = super.get(key);  
        if (value == null) 
        { throw new IllegalArgumentException(name + "不包含" + key + "的值"); }
        
        if (value instanceof Ambiguity) 
        { throw new IllegalArgumentException(((Ambiguity) value).getSubject() + "中的" + name + "不明确（请尝试使用包含命名空间的全名，或重命名其中一个条目）"); }
        
        return value;  
    }  

    private String getShortName(final String key) 
    {  
        final String[] keyparts = key.split("\\.");  
        return keyparts[keyparts.length - 1];  
    }  

    protected static class Ambiguity 
    {  
        private String subject;  

        public Ambiguity(final String subject) 
        { this.subject = subject; }  

        public String getSubject() 
        { return subject; }  
    }  
}