# Rapid Automation Framework

Automation framework to build end-to-end test

## Framework Feature
- Built-in BDD Support with Cucumber
- Automatic docker containerization of test environment
- Automatic browser driver detection and management
- Automatic video recording (configurable)
- Supports cross-browser execution and selenium-grid architecture
- Supports parallel, serial and hybrid test execution
- Supports partial execution using tagging (eq. @test, @qa_only, @ci_only)
- Powerful UI action library that performs automatic `explicit wait` and `retry` 
- Supports multi-format test reporting (JSON, Timeline, HTML-based, Cucumber-based)
- Supports multi-profile test execution with Maven
- Supports configuration injection for both Browser-related and Test-related
    - Sample browser-configuration: blocking Image, location, version, proxy, etc.
    - Sample test-configuration: video recording, selenium server, containerization, parallel settings.

## Getting Started

### Prerequisites
- Java 11.x
- Maven
- Docker (optional - only if you intend to automatic containerized the environment)

### Setting up
Open your Project's parent POM and add in following dependency:
```
    <dependency>
        <groupId>com.hendry</groupId>
        <artifactId>rapid-automation</artifactId>
        <version>version</version>
    </dependency>
```

### Examples
#### Installing Framework
- `$ git clone https://github.com/hendryang/rapid-automation.git`
- `$ cd ./rapid-automation`
- `$ mvn clean install`
#### Running Test 
- Note: 1 failure is expected to show sample of test failure.
- `$ cd ./rapid-automation/examples/framework-e2e-test`
- To Run all test without container with single thread (serial)
    - `$ mvn clean test -Dparallel=1`
- To Run all test in container (default: 5 threads parallel)
    - `$ mvn clean test -P container` 
- To Run all test in container with 3 threads (parallel)
    - `$ mvn clean test -P container -Dparallel=3`
- To Run all test in hybrid mode (parallel and serial)
    - annotate feature that you want to be in serial with `@serial`
    - `$ mvn clean test -Dparallel=5`
- Reports are generated in : `./rapid-automation/examples/target`    

### Built With
- Java 11.x
- Selenium WebDriver 4.x [UI]
- Rest-assured [API]
- Cucumber 5.x [BDD]
- Docker [Test Container]
- Bonigarcia 4.x [WebDriver management]

