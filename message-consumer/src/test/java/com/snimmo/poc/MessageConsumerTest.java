package com.snimmo.poc;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class MessageConsumerTest {

    @Inject @Any
    InMemoryConnector connector;

    @Test
    void testNormalFlow() {
        InMemorySource<Message<MessageValue>> messageIn = connector.source("message-in");
        InMemorySink<MessageValue> messageOut = connector.sink("message-out");
        MessageKey messageKey = new MessageKey();
        OutgoingKafkaRecordMetadata<?> metadata = OutgoingKafkaRecordMetadata.builder()
                .withKey(messageKey)
                .build();
        String value1 = UUID.randomUUID().toString();
        messageIn.send(Message.of(new MessageValue(value1)).addMetadata(metadata));
        await().<List<? extends Message<MessageValue>>>until(messageOut::received, t -> t.size() == 1);

        Message<MessageValue> message = messageOut.received().get(0);
        MessageValue messageValue = message.getPayload();
        assertThat(messageValue.getValue1()).isEqualTo(value1);
    }

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

}