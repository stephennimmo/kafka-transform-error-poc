package com.snimmo.poc;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Properties;

@ApplicationScoped
public class KafkaConsumerProducer {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @Produces
    public KafkaConsumer<MessageKey, MessageValue> kafkaConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("max.poll.records", "1");
        return new KafkaConsumer<MessageKey, MessageValue>(properties);
    }

}
