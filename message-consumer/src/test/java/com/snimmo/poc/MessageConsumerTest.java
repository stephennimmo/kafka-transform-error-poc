package com.snimmo.poc;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
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
        kafkaCompanion.produceWithSerializers(new ObjectMapperSerializer(), new ObjectMapperSerializer())
                        .fromRecords(new ProducerRecord("message-in", messageKey, messageValue))
                                .awaitCompletion(Duration.ofSeconds(10));

        ConsumerTask<MessageKey, MessageValue> messages = kafkaCompanion.consume(MessageKey.class, MessageValue.class)
                .withClientId("test")
                .withOffsetReset(OffsetResetStrategy.LATEST)
                .withAutoCommit()
                .fromTopics("message-out", 1);
        messages.awaitCompletion();
        ConsumerRecord<MessageKey, MessageValue> message = messages.getRecords().get(0);
        assertThat(message.value().getValue1()).isEqualTo(value1);
    }

    /*
    @Test
    void testFailFlow() {
        InMemoryConnector.clear();
        InMemorySource<Message<MessageValue>> messageIn = connector.source("message-in");
        InMemorySink<MessageValue> messageOut = connector.sink("message-out");
        MessageKey messageKey = new MessageKey();
        OutgoingKafkaRecordMetadata<?> metadata = OutgoingKafkaRecordMetadata.builder()
                .withKey(messageKey)
                .build();
        String value1 = UUID.randomUUID().toString();
        messageIn.send(Message.of(new MessageValue(value1)).addMetadata(metadata));
        messageIn.send(Message.of(new MessageValue("FAIL")).addMetadata(metadata));
        messageIn.send(Message.of(new MessageValue(value1)).addMetadata(metadata));
        await().<List<? extends Message<MessageValue>>>until(messageOut::received, t -> t.size() == 2);

        Message<MessageValue> message = messageOut.received().get(0);
        MessageValue messageValue = message.getPayload();
        assertThat(messageValue.getValue1()).isEqualTo(value1);
    }
    */

}