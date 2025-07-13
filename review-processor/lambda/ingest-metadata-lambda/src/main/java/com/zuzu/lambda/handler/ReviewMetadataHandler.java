package com.zuzu.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Map;

public class ReviewMetadataHandler implements RequestHandler<Map<String, Object>, String> {

    private KafkaProducer<String, String> producer;

    public ReviewMetadataHandler() {
        Properties props = new Properties();
        props.put("bootstrap.servers", System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        try {
            String metadataJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(input);
            producer.send(new ProducerRecord<>("review_ingest", metadataJson));
            return "Metadata published to Kafka successfully.";
        } catch (Exception e) {
            context.getLogger().log("Error sending metadata to Kafka: " + e.getMessage());
            return "Failed to publish metadata.";
        }
    }
}