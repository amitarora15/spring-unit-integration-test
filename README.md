Unit & Integration Testing using Spring Boot
---

A unit test covers a single “unit”, where a unit is a single class, or can be a cluster of cohesive classes that is tested in combination  excluding all other components that interact with our under testing one.

An integration test can be :
1. test that covers multiple “units”. It tests the interaction between two or more clusters of cohesive classes like Controller and ExceptionHandler
1. test that covers multiple layers. This is actually a specialization of the first case and might cover the interaction between a business service and the persistence layer, for instance. eg: DB layer and Database
1. test that covers the whole path through the application. In these tests, we send a request to the application and check that it responds correctly and has changed the database state according to our expectations.

Important Points:
1. Spring Framework does have a dedicated test module for integration testing. It is known as spring-test. If we are using spring-boot, then we need to use spring-boot-starter-test which will internally use spring-test and other dependent libraries (Junit, Mockito, Assert4J, Hamcrest, JSONAssert & JSONPath). 
1. AssertJ & Mockito are present in spring-boot-starter-test.
1. AssertJ - `assertThat(savedUser.getRegistrationDate()).isNotNull();`
1. Don't inject dependencies using @Autowire, instead use Constructor Injection using Lombok and final variables
1. For testing controllers and repositories we need to write integration test, as unit test can test only bean, but cannot test integration of HTTP with controller or database with repositories. A full end to end integration test will be different and required for end to end testing.
1. Static imports
    ```java
      import static org.assertj.core.api.Assertions.*;
      import static org.junit.jupiter.api.Assertions.*;
      import static org.mockito.Mockito.*;
      import static org.mockito.BDDMockito.*;  //for given then syntax instead of when and verify
      import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
      import static org.hamcrest.CoreMatchers.*;
1. @NoArgsConstructor is required for VO and entity
1. Generally we should write unit test (less time) and if we have to test integration between layers use @DataJpaTest or @WebMvcTest and to test whole application use @SpringBootTest. This is increasing order of time.
1. In addition to MVC, JPA and Spring boot test, there are lot of auto-configured test like @DataMongoTest, @WebFluxTest, @JdbcTest, @RestClientTest etc..
---
Controller test: (Regular @Component are not loaded, they need to be replaced with @MockBean)
1. On controller test class - `@WebMvcTest(controllers = <controller class>)`. Controller will say to only load the mentioned Controller in Application Context and all other controllers won't be loaded in context, thus each Controller test will run in separate application context which will have more runtime but less complex.
1. All mocking behaviour should be set before calling mvcResult.perform
1. Use ObjectMapper and MockMvc as @Autowired bean to handle serialization/deserialization and handle MVC request
1. Use @MockBean to mock business logic, it will automatically add/replaces the bean of the same type in the **application context** with a Mockito mock
1. Use @SpyBean to wrap the bean with Proxy which can delegate method call to bean unless mock behaviour of method defined
1. General Test places - Input, Valid Request (@Valid), Business logic mocking, output validation and exception handling
1.  Validation of JSON can be done using - `.andExpect(jsonPath("$[0].title", is("Hokuto no ken")))` or `.andExpect(jsonPath("$.*.title", hasItem(is("Hokuto no Ken"))));` or `.andExpect(jsonPath("$", hasSize(1)))` or  `.andExpect(jsonPath("$[0].name", is(alex.getName())));`
1. Sync and Async controllers (@Async and returning CompletableFuture) can be tested
1. Custom Result matchers can be created for comparing JSON output with expected object
```java 
    MvcResult mvcResult = mockMvc.perform(post("/forums/{forumId}/register", 42L)
    .contentType("application/json")
    .param("sendWelcomeMail", "true")
    .content(objectMapper.writeValueAsString(user)))
    .andExpect(status().isOk()).andReturn();
    ...
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    assertThat(objectMapper.writeValueAsString(expectedResponseBody))
     .isEqualToIgnoringWhitespace(actualResponseBody);
```
---

JPA Test (Regular @Component are not loaded, they need to be replaced with @MockBean)
1. Inferred Queries - like findByName - no need to write test for this as Spring Data automatically do this while booting
1. JPQL query - Hibernate validates query at start up, so no need to write test. 
1. Native Query - no validation by Spring data JPA and Hibernate, so test should be written against this
1. Some of the construct may not work against embedded DB (by default Jpa test point to embedded test, can be avoided by @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) in test class), so better to use Docker container or [test container](https://www.testcontainers.org/)  for DB in CI pipeline for test
1. @DataJpa test does following
    * configuring H2, an in-memory database
    * setting Hibernate, Spring Data, and the DataSource
    * performing an @EntityScan
    * turning on SQL logging
1. With @DataJpaTest, all of these DataSource, @JdbcTemplate or @EntityManager and other repositories will be available in context
1. Application context and in-memory DB is shared between all test, hence by default every transaction is rolled back after every test
1. Data JPA tests are transactional and roll back at the end of each test
1. Schema Creation:
    1. By default @DataJpaTest will have spring.jpa.hibernate.ddl-auto to create-drop
    1. It makes sense to set Hibernate’s ddl-auto configuration to validate when using a script or migration tool to initialize the schema
    1. schema.sql file in classpath
        1. spring.datasource.initialization-mode says when this schema.sql will be executed (default value is embedded other value can be always)
    1. Flyway - Database migration tool which keeps track which statements have been executed already and will not execute them again. Can be used in production as well.
        1. `compile('org.flywaydb:flyway-core')`
        1. SQL scripts present at `src/main/resources/db/migration`
        1. can be turned off by `spring.flyway.enabled=false` in our test cases
    1. Liquibase - Database migration tool which keeps track which statements have been executed already and will not execute them again. Can be used in production as well.  
        1. `compile('org.liquibase:liquibase-core')`
        1. Format can be XML or YAML and present at `src/main/resources/db/changelog/db.changelog-master.yaml`
        1. Difficult to manage, better to use Flyway
1. Schema population
    1. data.sql in classpath
        1. Insert statement to populate database
        1. Hard to maintain
    1. Inserting through code in our test before query
        1. Tiresome and complex for multiple entities
        1. Refactoring safe
    1. Use @Autowired TestEntityManager to persist data
    1. DB Unit and Spring DB Unit (**NOT MAINTAINED**)
        1. Gradle file
            ```groovy
              compile('com.github.springtestdbunit:spring-test-dbunit:1.3.0')
              compile('org.dbunit:dbunit:2.6.0')
        1. Create XML file in parallel to test class
            ```xml
               <?xml version="1.0" encoding="UTF-8"?>
               <dataset>
                   <user
                       id="1"
                       name="Zaphod Beeblebrox"
                       email="zaphod@galaxy.net"
                   />
               </dataset>   
        1. Test class
            ```java
               @DataJpaTest
               @TestExecutionListeners({
                       DependencyInjectionTestExecutionListener.class,
                       TransactionDbUnitTestExecutionListener.class
               })
        1. In your test class
           ```java
           @Test
           @DatabaseSetup("createUser.xml")     
    1. @Sql
        1. Create SQL file in classpath
            ```sql
               INSERT INTO USER 
                           (id, 
                            NAME, 
                            email) 
               VALUES      (1, 
                            'Zaphod Beeblebrox', 
                            'zaphod@galaxy.net');                
             
        1. In your test
            ```java
           @Test
           @Sql("createUser.sql")
           public void whenInitializedByDbUnit_thenFindsByName() {
               UserEntity user = userRepository.findByName("Zaphod Beeblebrox");
               assertThat(user).isNotNull();
             }   
        1. Use @SqlGroup to combine
---
Service Layer
1. Use @MockBean to mock Repository and test
1. Just write plain unit test cases without Spring by mocking Repository
---
@SpringBootTest  (use Tag to run these test separately as they are time consuming)      
1. Starts loading context by searching context from current test class upto parent package which has @SpringBootConfiguration (@SpringBootApplication)
1. Generally ony one context is created unless each test has different way of loading context, so better to move all configuration at one place to create one context
1. By default, @SpringBootTest  does not start a server unless attribute webEnvironment added to further refine how your tests run. It has several options:
    1. MOCK(Default): Loads a web ApplicationContext and provides a mock web environment. Use @AutoConfigureMockMvc to add a MockMvc instance to the application context (required for MockMvc). Test using @MockMvc hit a controller which will save record in database, and then using repository assert that data is saved or not. Server is not started.  If a web environment is not available on your classpath, this mode transparently falls back to creating a regular non-web ApplicationContext.
    1. RANDOM_PORT: Loads a WebServerApplicationContext and provides a real web environment by starting embedded server on a random port. Use `@Autowired private TestRestTemplate restTemplate;` and call service using RestTemplate and then assert on that. Capture port using @LocalPort and use it for making Rest call.
    1. DEFINED_PORT: Loads a WebServerApplicationContext and provides a real web environment. Use `@Autowired private TestRestTemplate restTemplate;` and call service using RestTemplate and then assert on that.
    1. NONE: Loads an ApplicationContext by using SpringApplication but does not provide any web environment
1. Customisation of Application Context created from @SpringBootTest is possible, but then settings will be different from Production environment
    1. Setting property values like - `@SpringBootTest(properties = "foo=bar")` 
    1. Expecting arguments - `@SpringBootTest(args = "--app.test=one")` and use it `void applicationArgumentsPopulated(@Autowired ApplicationArguments args) {`
    1. @ActiveProfiles to load other profile for tests
    1. `@TestPropertySource(locations = "/foo.properties")`
    1. Any part of application can be mocked by @MockBean like Redis layer can be mocked or repository can be mocked but server layer not
    1. If a class in not picked in Application Context, it can be loaded using @Import(.....class)
    1. Provides support for different web environment modes, including the ability to start a fully running web server listening on a defined or random port.
    1. Use @TestConfiguration to customise primary configuration
    1. If we need custom Application context, then it can also be created and loaded if present parallel to test class or in same package in test directory or overriden using `@SpringBootTest(classes = CustomApplication.class)`. Generally needed to override some settings like Scheduling and Retry present. This will create different application context compared to Production, so in these cases we should:
        1. Move @EnableScheduling to some other Configuration class
        1. Make it Conditional on Property. 
        1. Disable this property in test 
---
Rest Client
1. @RestClientTest("<name of rest client class>")
1. It provides `@Autowired private MockRestServiceServer server;`
1. Eg:
```java
    @Test
    void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails()
            throws Exception {
        this.server.expect(requestTo("/greet/details"))
                .andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
        String greeting = this.service.callRestService();
        assertThat(greeting).isEqualTo("hello");
    }
```        