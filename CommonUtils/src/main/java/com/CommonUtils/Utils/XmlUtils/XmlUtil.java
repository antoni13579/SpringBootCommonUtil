package com.CommonUtils.Utils.XmlUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.CommonUtils.Utils.CollectionUtils.JavaCollectionsUtil;
import com.CommonUtils.Utils.CommonUtils.CommonUtil;
import com.CommonUtils.Utils.XmlUtils.Bean.XmlNode;
import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class XmlUtil 
{
	private static XStream XSTREAM = null;
	
	private static SAXReader SAXREADER = null;
	
	private XmlUtil() {}
	
	private static XStream getInstanceForXStream()
	{
		if (null == XSTREAM)
		{
			synchronized (XmlUtil.class)
			{
				if (null == XSTREAM)
				{
					XSTREAM = new XStream();
					XSTREAM.autodetectAnnotations(true);
					XStream.setupDefaultSecurity(XSTREAM);
				}
			}
		}
		
		return XSTREAM;
	}
	
	private static SAXReader getInstanceForSAXReader()
	{
		if (null == SAXREADER)
		{
			synchronized (XmlUtil.class)
			{
				if (null == SAXREADER)
				{ SAXREADER = new SAXReader(); }
			}
		}
		
		return SAXREADER;
	}
	
	public static String beanToXml(final Object obj)
	{ return getInstanceForXStream().toXML(obj); }
	
	public static void beanToXml(final Object obj, final OutputStream os)
	{ getInstanceForXStream().toXML(obj, os); }
	
	public static void beanToXml(final Object obj, final Writer writer)
	{ getInstanceForXStream().toXML(obj, writer); }
	
	public static <T> Object xmlToBean(final Class<T> clazz, final String xml)
	{
		XStream xstream = getInstanceForXStream();
		xstream.allowTypeHierarchy(clazz);
		return xstream.fromXML(xml);
	}
	
	public static <T> Object xmlToBean(final Class<T> clazz, final File file)
	{
		XStream xstream = getInstanceForXStream();
		xstream.allowTypeHierarchy(clazz);
		return xstream.fromXML(file);
	}
	
	public static <T> Object xmlToBean(final Class<T> clazz, final InputStream is)
	{
		XStream xstream = getInstanceForXStream();
		xstream.allowTypeHierarchy(clazz);
		return xstream.fromXML(is);
	}
	
	public static <T> Object xmlToBean(final Class<T> clazz, final Reader reader)
	{
		XStream xstream = getInstanceForXStream();
		xstream.allowTypeHierarchy(clazz);
		return xstream.fromXML(reader);
	}
	
	private static void recursionGenerateNode(final XmlNode xmlNode)
	{
		Iterator<Element> externalIter = CommonUtil.cast(xmlNode.toElement().elementIterator());
		while (externalIter.hasNext())
		{
			if (JavaCollectionsUtil.isCollectionEmpty(xmlNode.getChildren()))
			{ xmlNode.setChildren(new ArrayList<>()); }
			
			Element element = externalIter.next();
			XmlNode paramXmlNode = new XmlNode(element);
			xmlNode.getChildren().add(paramXmlNode);
			
			Iterator<Element> internalIter = CommonUtil.cast(element.elementIterator());
			if (internalIter.hasNext())
			{ recursionGenerateNode(paramXmlNode); }
		}
	}
	
	public static Collection<XmlNode> getNodes(final String filePath)
	{ return getNodes(new File(filePath)); }
	
	public static Collection<XmlNode> getNodes(final Path path)
	{ return getNodes(path.toFile()); }
	
	public static Collection<XmlNode> getNodes(final File xmlFile)
	{
		try
		{
			Collection<XmlNode> result = new ArrayList<>();
			Iterator<Element> iter = CommonUtil.cast
			(
					getInstanceForSAXReader().read(xmlFile)
					   						 .getRootElement()
					   						 .elementIterator()
			);
			
			while (iter.hasNext())
			{
				Element element = iter.next();
				XmlNode xmlNode = new XmlNode(element);
				recursionGenerateNode(xmlNode);
				result.add(xmlNode);
			}
			
			return result;
		}
		catch (Exception ex)
		{
			log.error("根据XML文件，获取XML元素出现异常，文件名为{}，异常原因为", xmlFile.getAbsolutePath(), ex);
			return Collections.emptyList();
		}
	}
	
	public static String toJson(final Collection<XmlNode> xmlNodes)
	{ return JSON.toJSONString(xmlNodes); }
}