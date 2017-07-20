package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleDefinitionRegistryFactory;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.util.List;

public abstract class MockContextCustomizerFactory implements ContextCustomizerFactory {
	@Override
	public ContextCustomizer createContextCustomizer(Class<?> aClass, List<ContextConfigurationAttributes> list) {
		return createContextCustomizer(new DoubleDefinitionRegistryFactory().parse(aClass));
	}

	/**
	 * <p>Should create context customizer with all mock definitions configured in the application context.</p>
	 * @param doubleRegistry
	 * @return
	 */
	protected abstract ContextCustomizer createContextCustomizer(DoubleRegistry doubleRegistry);
}
