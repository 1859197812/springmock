package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(TestCaseConfigurationClassLevelDoublesShouldBeRegistered.ContextBootstrap.class)
public class TestCaseConfigurationClassLevelDoublesShouldBeRegistered {
	public static final String MOCK_NAME = "mock";
	public static final String SPY_NAME = "mock";

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void should_inject_mocks_into_application_context() {
		assertSame(
				FixedDoubleFactory.MOCK,
				applicationContext.getBean(MOCK_NAME));
	}

	@Test
	public void should_inject_spies_into_application_context() {
		assertSame(
				FixedDoubleFactory.SPY,
				applicationContext.getBean(SPY_NAME));
	}

	@Configuration
	@AutowiredMock(name = MOCK_NAME, doubleClass = TestCaseConfigurationClassLevelDoublesShouldBeRegistered.MyService.class)
	@AutowiredSpy(name = SPY_NAME, doubleClass = TestCaseConfigurationClassLevelDoublesShouldBeRegistered.MyService.class)
	static class Config {
	}

	static class MyService {
	}

	static class ContextBootstrap extends SpringMockContextBootstrapper {
		protected ContextBootstrap() {
			super(FixedDoubleFactory::new);
		}
	}
}
