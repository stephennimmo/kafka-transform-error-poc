package com.snimmo.poc;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class MessageKeyDeserializer extends ObjectMapperDeserializer<MessageKey> {
    public MessageKeyDeserializer() {
        super(MessageKey.class);
    }
}
