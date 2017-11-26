package com.tikalk.antspublisher.core;

import com.tikalk.antspublisher.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

public class ChatVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(Logger.class);


    @Override
    public void start(Future<Void> fut) throws Exception {
        Router router = Router.router(vertx);

        // Allow events for the designated addresses in/out of the event bus bridge
        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
                .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));

        // Create the event bus bridge and add it to the router.
        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
        router.route("/eventbus/*").handler(ebHandler);

        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create());

        // Start the web server and tell it to use the router to handle requests.
        vertx.createHttpServer().requestHandler(router::accept).listen(Constants.DEFAULT_PORT, res -> {
            if(res.succeeded()) {
                fut.complete();
            }
            else{
                fut.fail(fut.cause());
            }
        });

        EventBus eb = vertx.eventBus();

        // Register to listen for messages coming IN to the server
        eb.consumer("chat.to.server").handler(message -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
            // Send the address back out to all clients with the timestamp prepended.
            eb.publish("chat.to.client", timestamp + ": " + message.body());
        });

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ChatVerticle.class.getName(),
                new DeploymentOptions().setWorker(true),
                deployment -> {
                    logger.info("Deployed!");
                });


    }
}
