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

import com.CommonUtils.Utils.XmlUtils.Bean.XmlNode;

import com.thoughtworks.xstream.XStream;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class XmlUtil 
{	
	private XmlUtil() {}
	
	private static XStream getInstanceForXStream()
	{ return SingletonContainerForXStream.xstream; }
	
	private static SAXReader getInstanceForSAXReader()
	{ return SingletonContainerForSAXReader.saxReader; }
	
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
		Iterator<Element> externalIter = Convert.convert(new TypeReference<Iterator<Element>>() {}, xmlNode.toElement().elementIterator());
		
		while (externalIter.hasNext())
		{
			if (CollUtil.isEmpty(xmlNode.getChildren()))
			{ xmlNode.setChildren(new ArrayList<>()); }
			
			Element element = externalIter.next();
			XmlNode paramXmlNode = new XmlNode(element);
			xmlNode.getChildren().add(paramXmlNode);
			
			Iterator<Element> internalIter = Convert.convert(new TypeReference<Iterator<Element>>() {}, element.elementIterator());
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
			Iterator<Element> iter = Convert.convert(new TypeReference<Iterator<Element>>() {}, getInstanceForSAXReader().read(xmlFile).getRootElement().elementIterator());
			
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
	
	private static class SingletonContainerForSAXReader
	{ private static SAXReader saxReader = new SAXReader(); }
	
	private static class SingletonContainerForXStream
	{
		private static XStream xstream = new XStream();
		
		static
		{
			xstream.autodetectAnnotations(true);
			XStream.setupDefaultSecurity(xstream);
		}
	}
}