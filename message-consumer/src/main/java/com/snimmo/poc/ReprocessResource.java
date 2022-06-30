package com.snimmo.poc;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.Collections;

@Path("/reprocess")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReprocessResource {

    @Inject
    KafkaConsumer<MessageKey, MessageValue> kafkaConsumer;

    @Inject
    MessageConsumer messageConsumer;

    @Inject
    TransformationService transformationService;

    @POST
    public Response reprocess(ReprocessRequest reprocessRequest) {
        TopicPartition topicPartition = new TopicPartition("message-in", reprocessRequest.getPartition());
        kafkaConsumer.assign(Collections.singletonList(topicPartition));
        kafkaConsumer.seek(topicPartition, reprocessRequest.getOffset());

        ConsumerRecords<MessageKey, MessageValue> records = kafkaConsumer.poll(Duration.ofSeconds(5));
        ConsumerRecord<MessageKey, MessageValue> consumerRecord = records.iterator().next();
        MessageKey messageKey = consumerRecord.key();
        MessageValue messageValue = consumerRecord.value();
        OutgoingKafkaRecordMetadata<?> metadata = OutgoingKafkaRecordMetadata.builder()
                .withKey(messageKey)
                .build();
        messageConsumer.handleMessage(Message.of(messageValue).addMetadata(metadata));
        return Response.ok().build();
    }

    @POST
    @Path("/flag")
    public Response flag(boolean newFlag) {
        this.transformationService.setFail(newFlag);
        return Response.ok().build();
    }


}