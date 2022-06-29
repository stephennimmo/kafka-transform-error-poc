package com.snimmo.poc;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    @Inject
    @Channel("message-out")
    Emitter<MessageValue> messageOutEmitter;

    @Incoming("message-in")
    public CompletionStage<Void> handleMessage(Message<MessageValue> in) {
        log.info(in.getMetadata().toString());
        if (!Objects.equals(in.getPayload().getValue1(), "FAIL")) {
            messageOutEmitter.send(in);
        }
        return in.ack();
    }

}
