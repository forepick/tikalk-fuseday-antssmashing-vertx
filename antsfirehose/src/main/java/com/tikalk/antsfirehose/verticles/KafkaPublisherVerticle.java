package com.tikalk.antsfirehose.verticles;

import com.tikalk.antsfirehose.Constants;
import com.tikalk.antsfirehose.core.FireHose;
import com.tikalk.antsfirehose.models.Game;
import com.tikalk.antsfirehose.models.GameState;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaWriteStream;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class KafkaPublisherVerticle extends AbstractVerticle {
    final private static Logger LOGGER = LogManager.getLogger(KafkaPublisherVerticle.class);

    private KafkaWriteStream<String, String> producer;

    @Override
    public void start() throws Exception {
        Map<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put("bootstrap.servers", "localhost:9092");
        kafkaConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.put("group.id", Constants.KAFKA_GROUP_ID);
        kafkaConfig.put("auto.offset.reset", "earliest");
        kafkaConfig.put("enable.auto.commit", "false");

        // Create the producer
        producer = KafkaWriteStream.create(vertx, kafkaConfig, String.class, String.class);

        vertx.setPeriodic(10, id -> {
            Game game = FireHose.generate();
            if (game == null || game.isFinishSent()) {
                return;
            }
            JsonObject message = new JsonObject(Json.encode(game));
            producer.write(new ProducerRecord<>(Constants.KAFKA_ANTS_TOPIC, message.toString()));
            if (game.getState() == GameState.FINISHED || game.getState() == GameState.STOPPED) {
                game.setFinishSent(true);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        if (producer != null) {
            producer.close();
        }
    }
}
