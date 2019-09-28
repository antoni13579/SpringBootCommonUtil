package com.CommonUtils.Utils.XmlUtils.Bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.CommonUtils.Utils.DataTypeUtils.StringUtils.StringUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ToString
public class XmlNode
{
	@Getter
	@Setter
	private String nodeName;
	
	@Getter
	@Setter
	private String nodeValue;

	private Element element;
	
	@Getter
	@Setter
	private Collection<XmlAttribute> xmlAttributes;
	
	@Getter
	@Setter
	private Collection<XmlNode> children;
	
	public XmlNode(@NonNull final Element element)
	{
		this.nodeName = element.getName();
		this.nodeValue = !StringUtil.isStrEmpty(element.getTextTrim()) ? element.getTextTrim() : "";
		this.element = element;

		Iterator<Attribute> iter = Convert.convert(new TypeReference<Iterator<Attribute>>() {}, element.attributeIterator());
		while (iter.hasNext())
		{
			Attribute attribute = iter.next();
			if (CollUtil.isEmpty(this.xmlAttributes)) { this.xmlAttributes = new ArrayList<>(); }
			this.xmlAttributes.add(new XmlAttribute(attribute));
		}
	}
	
	public boolean haveChildren()
	{ return !CollUtil.isEmpty(this.children); }
	
	public Element toElement()
	{ return this.element; }
}