package com.snimmo.poc;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class OrderProcessorTest {

    @InjectKafkaCompanion 
    KafkaCompanion companion;

    @Test
    void testProcessor() {
        companion.produceStrings().usingGenerator(i -> new ProducerRecord<>("orders", UUID.randomUUID().toString())); 

        // Expect that the tested application processes orders from 'orders' topic and write to 'orders-processed' topic

        ConsumerTask<String, String> orders = companion.consumeStrings().fromTopics("orders-processed", 10); 
        orders.awaitCompletion(); 
        assertEquals(10, orders.count());
    }
}