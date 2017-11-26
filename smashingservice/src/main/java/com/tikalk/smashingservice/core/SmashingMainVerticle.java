package com.tikalk.smashingservice.core;

import io.vertx.core.AbstractVerticle;

public class SmashingMainVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		vertx
			.deployVerticle(new SmashingKafkaStreamVerticle());

	}
}