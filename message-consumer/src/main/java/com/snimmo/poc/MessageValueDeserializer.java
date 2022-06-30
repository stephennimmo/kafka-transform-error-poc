package com.snimmo.poc;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class MessageValueDeserializer extends ObjectMapperDeserializer<MessageValue> {
    public MessageValueDeserializer() {
        super(MessageValue.class);
    }
}
