package com.CommonUtils.Utils.XmlUtils.Bean;

import org.dom4j.Attribute;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ToString
public class XmlAttribute 
{
	@Getter
	@Setter
	private String attributeName;
	
	@Getter
	@Setter
	private String attributeValue;
	
	private Attribute attribute;
	
	public XmlAttribute(@NonNull final Attribute attribute)
	{
		this.attributeName = attribute.getName();
		this.attributeValue = attribute.getValue();
		this.attribute = attribute;
	}
	
	public Attribute toAttribute()
	{ return this.attribute; }
}