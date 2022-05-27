package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {Thing.class},
    properties = {
        "embedded.mongodb.enabled=true",
        "spring.data.mongodb.uri=mongodb://${embedded.mongodb.host}:${embedded.mongodb.port}/${embedded.mongodb.database}",
    }
)
@EnableAutoConfiguration
@ActiveProfiles({"test"})
class BugTest {

    @Autowired
    @Qualifier("msgChannel")
    DirectChannel msgChannel;

    @Autowired
    @Qualifier("resultChannel")
    QueueChannel resultChannel;

    @Test
    void testThing() {
        Assertions.assertTrue(msgChannel.send(MessageBuilder.withPayload("Hi!").build()));
        Assertions.assertNotNull(resultChannel.receive(5000));
    }

}
