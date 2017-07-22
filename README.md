# springmock

[![Build Status](https://travis-ci.org/pchudzik/springmock.svg?branch=master)](https://travis-ci.org/pchudzik/springmock)

## Introduction

Alternative spring mocking infrastructure. With pluggable mocking library support. Purpose is to
allow you to easily create and inject mocks of any mocking library into your spring tests. Currently
mockito and spock mocks are supported.

Why? Spring boot supports only mockito as mocking library which is great if you write tests in java.
When using spock you might want to use spock mocks because of syntactic sugar they offer. My
motivation was to extend @MockBean and @SpyBean behaviour of spring-boot-test and allow to easily
inject mocks from library of your choice.

Note that this is not yet final release so if you decide to use it you do it on your own risk.

## Contents

* [Introduction](#introduction)
* [Contents](#contents)
* [Requirements](#requirements)
  * [Mockito](#mockito)
  * [Spock](#spock)
* [Installation](#installation)
  * [Releases](#releases)
    * [With Mockito as mocks provider](#with-mockito-as-mocks-provider)
      * [mvn](#mvn)
      * [gradle](#gradle)
    * [With Spock as mock provider](#with-spock-as-mock-provider)
      * [mvn](#mvn-1)
      * [gradle](#gradle-1)
  * [Snapshots](#snapshots)
    * [Repository configuration](#repository-configuration)
    * [Mockito as mocks provider](#mockito-as-mocks-provider)
    * [Spock as mocks provider](#spock-as-mocks-provider)
* [Usage](#usage)
  * [Mocks](#mocks)
  * [Spies](#spies)
* [Problems](#problems)
* [Changelog](#changelog)
  * [1.0.0 - 2017.07.22](#100---20170722)


## Requirements

Basic requirements to make it running:

* java8
* spring <= 4.3.6
* springboot <= 1.4.4

Required spring modules: spring-context & spring-test

### Mockito

mockito-core <= 1.10.x is required and must be provided. [Samples with spring-boot](samples/mockito-samples)

### Spock

To get spock mocks running you'll need:

* spock-core 1.1-groovy-2.4
* spock-spring 1.1-groovy-2.4

[Samples with spring-boot](samples/spock-samples)

## Installation

### Releases

#### With Mockito as mocks provider

##### mvn

```xml
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-mockito</artifactId>
  <version>1.0.0</version>
</dependency>
```

##### gradle

```java
testCompile('com.pchudzik.springmock:springmock-mockito:1.0.0')
``` 

#### With Spock as mock provider 

##### mvn

```xml
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-spock</artifactId>
  <version>1.0.0</version>
</dependency>
```

##### gradle

```
testCompile('com.pchudzik.springmock:springmock-mockito:1.0.0')
```

[sample pom.xml with spring-boot](samples/mockito-samples/pom.xml)

[sample build.gradle](samples/spock-samples/build.gradle)

### Snapshots

#### Repository configuration

Add [sonatype snapshots](https://oss.sonatype.org/content/repositories/snapshots) repository to
repositories list

Maven:
```
<repositories>
  <repository>
    <id>sonatype-snapshots</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```

Gradle:
```
repositories {
  maven {
    url 'https://oss.sonatype.org/content/repositories/snapshots'
  }
  mavenCentral()
}
```

#### Mockito as mocks provider

Include mvn dependency:
```
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-mockito</artifactId>
  <version>1.0.1-SNAPSHOT</version>
</dependency>
```

Or gradle dependency:

```
testCompile('com.pchudzik.springmock:springmock-mockito:1.0.1-SNAPSHOT')
```

#### Spock as mocks provider

Include mvn dependency:
```
<dependency>
  <groupId>com.pchudzik.springmock</groupId>
  <artifactId>springmock-spock</artifactId>
  <version>1.0.1-SNAPSHOT</version>
</dependency>
```

Or gradle dependency:

```
testCompile('com.pchudzik.springmock:springmock-spock:1.0.1-SNAPSHOT')
```

## Usage

Mock injection infrastructure is the same and is independent to selected mock provider.

Once mock is injected you can use it accordingly to selected mocking library [for
mockito](samples/mockito-samples/src/test/java/com/pchudzik/springmock/samples/mockito/MockitoSamplesApplicationTests.java)
and [for
spock](samples/spock-samples/src/test/groovy/com/pchudzik/springmock/samples/spock/SpockSamplesApplicationTest.groovy)

### Mocks

To inject mocks just annotate field you want to be initialized and injected with mock using @AutowiredMock and you are
good to go

```java
@AutowiredMock AnyService anyService;

@Test
public void should_inject_mock() {
  assertTrue(mockingDetails(anyService).isMock());
}
```

or in spock:
```
@AutowiredMock AService service

def "should inject mock"() {
  given: service.hello() >> "mock"
  when:  final result = service.hello()
  then:  result == "mock"
}
```

You can specify name of the registered mock using name param. Which will result in registering mock with the specific
name in the spring context (like in with
[@Bean#name](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html#name--)).
If name is not provided bean name will be the same ase the field name on which it is declared.

You can also provide list of aliases for mock using alias attribute.

[Sample test case with mockito mocks](samples/mockito-samples/src/test/java/com/pchudzik/springmock/samples/mockito/MockitoSamplesApplicationTests.java)

[Sample test case with spock mocks](samples/spock-samples/src/test/groovy/com/pchudzik/springmock/samples/spock/SpockSamplesApplicationTest.groovy)

### Spies

Right now springmock can not create spies on the fly and inject them in the context. In order to spy on the bean it must
already be registered in the context. It needs appropriate object to already exist in spring context.

```java
@AutowiredSpy Service service;

@Test
public void should_inject_spy() {
  assertTrue(mockingDetails(service).isSpy());
}
```

or in spock:

```
@AutowiredSpy Service service

def "should inject spy"() {
  when: service.hello()
  then: 1 * service.hello() >> "spy!"
}
```

You can specify name of the bean which should be replaced by created spy using name attribute. if no name is defined
then destination bean will be matched field name or by class.

## Problems

Please report any problems with the library using Github issues. I'd really appreciate failing test case included in
issue description or as PR.

## Changelog

### 1.0.0 - 2017.07.22

  * @AutowiredMock with name and aliases support
  * @AutowiredSpy with name and aliases support
