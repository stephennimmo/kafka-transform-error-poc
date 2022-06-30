package com.snimmo.poc;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class MessageConsumerTest {

    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;

    @BeforeEach
    public void beforeEach() {
        kafkaCompanion.registerSerde(MessageKey.class, new ObjectMapperSerializer(), new MessageKeyDeserializer());
        kafkaCompanion.registerSerde(MessageValue.class, new ObjectMapperSerializer(), new MessageValueDeserializer());
    }

    @Test
    void testNormalFlow() {
        String value1 = UUID.randomUUID().toString();
        MessageKey messageKey = new MessageKey(value1);
        MessageValue messageValue = new MessageValue(value1);
        kafkaCompanion.produce(MessageKey.class, MessageValue.class)
                .fromRecords(new ProducerRecord("message-in", messageKey, messageValue))
                .awaitCompletion(Duration.ofSeconds(5));
        ConsumerTask<MessageKey, MessageValue> messages = kafkaCompanion.consume(MessageKey.class, MessageValue.class)
                //.withClientId("test")
                //.withOffsetReset(OffsetResetStrategy.LATEST)
                //.withAutoCommit()
                .fromTopics("message-out", 1);
        messages.awaitCompletion();
        ConsumerRecord<MessageKey, MessageValue> message = messages.getRecords().get(0);
        assertThat(message.value().getValue1()).isEqualTo(value1);
    }

    @Test
    void failFlow() {
        kafkaCompanion.produce(MessageKey.class, MessageValue.class)
                .fromRecords(
                        new ProducerRecord("message-in", new MessageKey(UUID.randomUUID().toString()), new MessageValue(RandomStringUtils.randomAlphabetic(10))),
                        new ProducerRecord("message-in", new MessageKey(UUID.randomUUID().toString()), new MessageValue("FAIL")),
                        new ProducerRecord("message-in", new MessageKey(UUID.randomUUID().toString()), new MessageValue(RandomStringUtils.randomAlphabetic(10)))
                )
                .awaitCompletion(Duration.ofSeconds(5));
        ConsumerTask<MessageKey, MessageValue> messages = kafkaCompanion.consume(MessageKey.class, MessageValue.class)
                .withOffsetReset(OffsetResetStrategy.LATEST)
                .fromTopics("message-out", 3);
        messages.awaitCompletion();
        assertThat(messages.count()).isEqualTo(3);
    }

}