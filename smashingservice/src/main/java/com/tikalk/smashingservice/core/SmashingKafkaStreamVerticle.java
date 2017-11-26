package com.tikalk.smashingservice.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.tikalk.smashingservice.Constants;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaWriteStream;

public class SmashingKafkaStreamVerticle extends AbstractVerticle {

	private KafkaWriteStream<String, String> producer;

	@Override
	public void start() throws Exception {
		vertx
			.eventBus()
			.consumer("smashingMessage", this::handleSmashing);
		
		// Get the kafka producer config
		Map<String, Object> kafkaConfig = new HashMap<>();
		kafkaConfig.put("bootstrap.servers", "localhost:9092");
		kafkaConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaConfig.put("group.id", Constants.KAFKA_GROUP_ID);
		kafkaConfig.put("auto.offset.reset", "earliest");
		kafkaConfig.put("enable.auto.commit", "false");

		// Create the producer
		producer = KafkaWriteStream.create(vertx, kafkaConfig, String.class, String.class);
	}
	
	private void handleSmashing(Message<JsonObject> smashingMessage) {
		// Publish the metircs in Kafka
		JsonObject smashingKafkaMessage = new JsonObject();
		smashingKafkaMessage.put("antId", smashingMessage.body().getInteger("antId"));
		smashingKafkaMessage.put("playerId", smashingMessage.body().getInteger("playerId"));
		smashingKafkaMessage.put("gameId", smashingMessage.body().getInteger("gameId"));
		producer.write(new ProducerRecord<>("smashing_topic", smashingKafkaMessage.encode()));
	}
}
