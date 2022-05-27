# Reproduce instructions

This is the code of interest:

```java
a.releaseStrategy(x -> {  
    x.getMessages().forEach(m -> System.err.println("This works: " + m.getPayload().toString()));
    x.getMessages().stream().forEach(m -> System.err.println("This blows up: " + m.getPayload().toString()));
    return true;
});
```

Attempting to iterate over a `.stream()` on the result of `.getMessage()` create an error when using Spring Boot 2.7.0 (Spring Integration 5.5.12).

## Demo

Run:

```bash
mvn test
```

Output:

```
...

[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running test.BugTest

...

This works: Hi!
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 7.158 s <<< FAILURE! - in test.BugTest
[ERROR] testThing  Time elapsed: 0.643 s  <<< ERROR!
org.springframework.messaging.MessageHandlingException: error occurred in message handler [bean 'flow.aggregator#0' for component 'flow.org.springframework.integration.config.ConsumerEndpointFactoryBean#0'; defined in: 'test.Thing'; from source: 'bean method flow']; nested exception is org.springframework.data.mapping.MappingException: No property message found on entity class org.springframework.integration.mongodb.store.MongoDbMessageStore$MessageWrapper to bind constructor parameter to!
        at test.BugTest.testThing(BugTest.java:35)
Caused by: org.springframework.data.mapping.MappingException: No property message found on entity class org.springframework.integration.mongodb.store.MongoDbMessageStore$MessageWrapper to bind constructor parameter to!
        at test.BugTest.testThing(BugTest.java:35)

...

[INFO] Results:
[INFO] 
[ERROR] Errors: 
[ERROR]   BugTest.testThing:35 » MessageHandling error occurred in message handler [bean...
[INFO] 
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.342 s
[INFO] Finished at: 2022-05-27T21:29:47+10:00
[INFO] ------------------------------------------------------------------------
```

Edit `pom.xml` to change Spring Boot version from 2.7.0 to 2.6.7:

```bash
sed -i s/2.7.0/2.6.7/g pom.xml
```

Run again:

```bash
mvn test
```

Result:

```
...
This works: Hi!
This blows up: Hi!
...
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.461 s
[INFO] Finished at: 2022-05-27T21:34:08+10:00
[INFO] ------------------------------------------------------------------------

```
