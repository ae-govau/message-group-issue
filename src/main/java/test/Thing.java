package test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mongodb.store.MongoDbMessageStore;

@Configuration
public class Thing {

    @Bean
    MongoDbMessageStore mongoMessageStore(MongoDatabaseFactory df) {
        return new MongoDbMessageStore(df, "foo");
    }

    @Bean
    DirectChannel msgChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    QueueChannel resultChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    IntegrationFlow flow(MongoDbMessageStore mms, @Qualifier("msgChannel") DirectChannel from, @Qualifier("resultChannel") QueueChannel result) {
        return IntegrationFlows.from(from)
            .aggregate(a -> {
                a.messageStore(mms);
                a.correlationStrategy(x -> "foo");
                a.releaseStrategy(x -> {  
                    x.getMessages().forEach(m -> System.err.println("This works: " + m.getPayload().toString()));
                    x.getMessages().stream().forEach(m -> System.err.println("This blows up: " + m.getPayload().toString()));
                    return true;
                });
                a.outputProcessor(mg -> mg.getOne().getPayload());
            })
            .channel(result)
            .get();
    }
}
