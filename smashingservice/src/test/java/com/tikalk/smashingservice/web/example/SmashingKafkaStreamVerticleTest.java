package com.tikalk.smashingservice.web.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tikalk.smashingservice.core.SmashingKafkaStreamVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class SmashingKafkaStreamVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context){
        vertx = Vertx.vertx();
        vertx.deployVerticle(
        		SmashingKafkaStreamVerticle.class.getName(),
                context.asyncAssertSuccess());
    }
    @After
    public void tearDown(TestContext context){
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testPublishSmashingMessage(TestContext context){
		JsonObject smashingMessage = new JsonObject();
		smashingMessage.put("antId", 1);
		smashingMessage.put("playerId", 2);
		smashingMessage.put("gameId", 3);
        vertx
			.eventBus()
			.publish("smashingMessage", smashingMessage);
    }
}
