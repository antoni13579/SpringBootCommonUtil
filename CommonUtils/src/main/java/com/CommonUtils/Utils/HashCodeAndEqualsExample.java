package com.CommonUtils.Utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class HashCodeAndEqualsExample 
{
	private String id;
    private String name;
    private String age;
    
    @Override
    public boolean equals(Object obj)
    {
    	//instanceof 已经处理了obj = null的情况
    	if(!(obj instanceof HashCodeAndEqualsExample)) 
    	{ return false; }
    	
    	//地址相等
    	if (this == obj)
    	{ return true; }
    	
    	HashCodeAndEqualsExample hashCodeAndEqualsExample = (HashCodeAndEqualsExample)obj;
    	return this.name.equals(hashCodeAndEqualsExample.name) && this.age.equals(hashCodeAndEqualsExample.age);
    }
    
    @Override
    public int hashCode()
    {
    	int result = 17;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (age == null ? 0 : age.hashCode());
        return result;
    }
}