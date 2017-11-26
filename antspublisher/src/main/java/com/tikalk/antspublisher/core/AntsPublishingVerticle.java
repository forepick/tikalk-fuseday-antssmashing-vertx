package com.tikalk.antspublisher.core;

import com.tikalk.antspublisher.Constants;
import com.tikalk.antspublisher.core.model.LocationReport;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;

public class AntsPublishingVerticle extends AbstractVerticle {

    Vertx vertx = Vertx.vertx();
    static Logger logger = LoggerFactory.getLogger(AntsPublishingVerticle.class);

    @Override
    public void start(Future<Void> fut) throws Exception {
        Router router = Router.router(vertx);
        router.route("/client.register/*").handler(eventBusHandler());


        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(Constants.DEFAULT_PORT, result -> {
                    if(result.succeeded()){
                        logger.info("server is listening");
                        fut.complete();
                    }
                    else{
                        fut.fail(result.cause());
                    }

                });
        vertx.setPeriodic(1000, t -> {
            LocationReport lr = new LocationReport();
            vertx.eventBus().publish(Constants.LOCATION_EVENTBUS_ADDRESS, lr.asEventBusMessage());
        });

    }

    private SockJSHandler eventBusHandler() {

        BridgeOptions options = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex(Constants.LOCATION_EVENTBUS_ADDRESS));
        return SockJSHandler.create(
                    vertx,
                    new SockJSHandlerOptions().setHeartbeatInterval(1000)
                ).bridge(options, event -> {
            BridgeEventType type = event.type();
            if(type == BridgeEventType.REGISTER){
                logger.info("Register");
            }
            if (type == BridgeEventType.SOCKET_CREATED) {
                logger.info("A socket was created");
            }
            if (type == BridgeEventType.RECEIVE){
                logger.info("event received");
            }
            event.complete(true);
        });

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(AntsPublishingVerticle.class.getName(),
                new DeploymentOptions().setWorker(true),
                deployment -> {
            if(deployment.succeeded()){
                logger.info("Verticle deployed");

                new TestAntsClient().run();

                //client.dispatcher().executorService().shutdown();

            }
            else{
                logger.info("failure", deployment.cause());
            }
        });
    }
}
