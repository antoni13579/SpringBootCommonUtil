package com.CommonUtils.Utils.TemplateEnginesUtils;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.CommonUtils.Utils.DataTypeUtils.CollectionUtils.JavaCollectionsUtil;

public final class ThymeleafUtil 
{
	private ThymeleafUtil() {}
	
	public static String process(final String template, final Map<String, Object> attributes, final TemplateEngine templateEngine)
	{
		Context context = new Context();
		JavaCollectionsUtil.mapProcessor
		(
				attributes, 
				(String key, Object value, int indx) -> 
				{ context.setVariable(key, value); }
		);
		
		return templateEngine.process(template, context);
	}
}