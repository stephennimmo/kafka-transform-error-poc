package com.snimmo.poc;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
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

    @Inject
    TransformationService transformationService;

    @Incoming("message-in")
    public CompletionStage<Void> handleMessage(Message<MessageValue> in) {
        try {
            MessageValue transformed = transformationService.transform(in.getPayload());
            messageOutEmitter.send(in);
        } catch (TransformationException e) {
            log.error("Transformation failed");
        }
        return in.ack();
    }

}
