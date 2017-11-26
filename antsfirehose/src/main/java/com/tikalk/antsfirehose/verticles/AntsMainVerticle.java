package com.tikalk.antsfirehose.verticles;

import com.tikalk.antsfirehose.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AntsMainVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(AntsMainVerticle.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpServerVerticle.class.getName(),
                new DeploymentOptions().setWorker(true),
                reply -> {
                    DeploymentOptions opts = new DeploymentOptions().setWorker(true).setInstances(1);
                    LOGGER.info("deploying client verticles");
                    vertx.deployVerticle(HttpServerVerticle.class.getName(), opts, deployment -> {
                        if (deployment.succeeded()) {
                            LOGGER.info("client verticles deployed.");
                        }
                    });
                });


        vertx.deployVerticle(KafkaPublisherVerticle.class.getName(),
                new DeploymentOptions().setWorker(true),
                reply -> {
                    DeploymentOptions opts = new DeploymentOptions().setWorker(true).setInstances(1);
                    LOGGER.info("deploying client verticles");
                    vertx.deployVerticle(KafkaPublisherVerticle.class.getName(), opts, deployment -> {
                        if (deployment.succeeded()) {
                            LOGGER.info("client verticles deployed.");
                        }
                    });
                });
    }
}
