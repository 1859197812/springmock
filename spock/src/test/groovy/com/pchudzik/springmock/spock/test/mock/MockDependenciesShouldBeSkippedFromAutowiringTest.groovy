package com.pchudzik.springmock.spock.test.mock

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock
import com.pchudzik.springmock.spock.test.mock.infrastructure.AnyService
import org.spockframework.mock.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
class MockDependenciesShouldBeSkippedFromAutowiringTest extends Specification {
	@AutowiredMock
	DestinationService service;

	def "should inject mock and skip dependency injection"() {
		expect:
		new MockUtil().isMock(service)
	}

	static class DestinationService {
		@Autowired
		private AnyService anyService;

		String toStuff() {
			anyService.hello()
		}
	}
}
