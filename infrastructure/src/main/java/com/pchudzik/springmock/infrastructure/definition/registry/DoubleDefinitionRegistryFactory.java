package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleNameResolver.resolveDoubleName;
import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Parses @{@link AutowiredMock} and @{@link AutowiredSpy} and creates double definitions stored in
 * the registry
 */
public class DoubleDefinitionRegistryFactory {
	private final Class<? extends Annotation> configurationAnnotation;
	private final DoubleConfigurationParser<?, Annotation> configurationParser;

	public DoubleDefinitionRegistryFactory(Class<? extends Annotation> configurationAnnotation, DoubleConfigurationParser configurationParser) {
		this.configurationAnnotation = configurationAnnotation;
		this.configurationParser = configurationParser;
	}

	public DoubleRegistry parse(Class<?> clazz) {
		final Set<DoubleDefinition> mocks = new HashSet<>();
		final Set<DoubleDefinition> spies = new HashSet<>();

		doWithFields(clazz, (Field field) -> {
			extractMockDefinition(field).ifPresent(mocks::add);
			extractSpyDefinition(field).ifPresent(spies::add);
		});

		return new DoubleRegistry(mocks, spies);
	}

	protected DoubleDefinition createMockDefinition(Field field, AutowiredMock autowiredMock) {
		final String doubleName = resolveDoubleName(field);
		final DoubleDefinition.DoubleDefinitionBuilder definitionBuilder = DoubleDefinition.builder()
				.doubleClass(field.getType())
				.name(doubleName)
				.aliases(asList(autowiredMock.alias()));

		applyDoubleConfiguration(
				field,
				configuration -> definitionBuilder.doubleConfiguration(configurationParser.parseMockConfiguration(doubleName, configuration)));

		return definitionBuilder.build();
	}

	private void applyDoubleConfiguration(Field field, Consumer<Annotation> configurationApplier) {
		final Annotation configuration = getAnnotation(field, configurationAnnotation);
		configurationApplier.accept(configuration);
	}

	protected DoubleDefinition createSpyDefinition(Field field, AutowiredSpy autowiredSpy) {
		final String doubleName = resolveDoubleName(field);
		final DoubleDefinition.DoubleDefinitionBuilder definitionBuilder = DoubleDefinition.builder()
				.doubleClass(field.getType())
				.name(doubleName)
				.aliases(asList(autowiredSpy.alias()));

		applyDoubleConfiguration(
				field,
				configuration -> definitionBuilder.doubleConfiguration(configurationParser.parseSpyConfiguration(doubleName, configuration)));

		return definitionBuilder.build();
	}

	private Optional<DoubleDefinition> extractMockDefinition(Field field) {
		return Optional
				.ofNullable(getAnnotation(field, AutowiredMock.class))
				.map(autowiredMock -> createMockDefinition(field, autowiredMock));
	}

	private Optional<DoubleDefinition> extractSpyDefinition(Field field) {
		return Optional
				.ofNullable(getAnnotation(field, AutowiredSpy.class))
				.map(autowiredSpy -> createSpyDefinition(field, autowiredSpy));
	}
}
