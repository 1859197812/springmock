package com.pchudzik.springmock.infrastructure.spring.test;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class ApplicationContextCreator {
	public static ApplicationContext buildAppContext(Stream<Entry<String, Object>> beans, Collection<BeanFactoryPostProcessor> postProcessors) {
		return buildAppContext(null, asMap(beans), postProcessors);
	}

	public static ApplicationContext buildAppContext(Stream<Entry<String, Object>> beans) {
		return buildAppContext(null, asMap(beans));
	}

	public static ApplicationContext buildAppContext(Map<String, Object> beans) {
		return buildAppContext(null, beans);
	}

	public static ApplicationContext buildAppContext(ApplicationContext parent, Stream<Entry<String, Object>> beans) {
		return buildAppContext(parent, asMap(beans));
	}

	public static Entry<String, Object>  withDoubleRegistry(DoubleRegistry registry) {
		return new SimpleEntry<>(DoubleRegistry.BEAN_NAME, registry);
	}

	public static ApplicationContext buildAppContext(ApplicationContext parent, Map<String, Object> beans) {
		return buildAppContext(parent, beans, Collections.emptyList());
	}

	public static ApplicationContext buildAppContext(ApplicationContext parent, Stream<Entry<String, Object>> beans, Collection<BeanFactoryPostProcessor> postProcessors) {
		return buildAppContext(parent, asMap(beans), postProcessors);
	}

	public static ApplicationContext buildAppContext(ApplicationContext parent, Map<String, Object> beans, Collection<BeanFactoryPostProcessor> postProcessors) {
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory, parent);

		postProcessors.forEach(applicationContext::addBeanFactoryPostProcessor);

		beans.entrySet()
				.forEach(entry -> {
					final String factoryBean = entry.getKey() + "_factory";
					beanFactory.registerSingleton(factoryBean, (Supplier<Object>) entry::getValue);
					beanFactory.registerBeanDefinition(entry.getKey(), BeanDefinitionBuilder
							.rootBeanDefinition(entry.getValue() != null ? entry.getValue().getClass() : Object.class)
							.setFactoryMethodOnBean("get", factoryBean)
							.getBeanDefinition());
				});

		applicationContext.refresh();

		return applicationContext;
	}

	private static Map<String, Object> asMap(Stream<Entry<String, Object>> beans) {
		return beans.collect(toMap(Entry::getKey, Entry::getValue));
	}
}
