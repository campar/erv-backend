package com.example.erv.config;

import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalTimezoneBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String zoneId = beanFactory.resolveEmbeddedValue("${app.configuration.timezone-id}");
		if (StringUtils.isBlank(zoneId)) zoneId = "Europe/Belgrade";
		TimeZone.setDefault(TimeZone.getTimeZone(zoneId));
	}
}
