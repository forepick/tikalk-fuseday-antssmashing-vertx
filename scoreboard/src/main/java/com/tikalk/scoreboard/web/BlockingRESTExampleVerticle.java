package com.tikalk.scoreboard.web;

import com.tikalk.scoreboard.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.annotation.PostConstruct;

public class BlockingRESTExampleVerticle extends AbstractVerticle {

    @Override
    @PostConstruct
    public void start(Future<Void> fut) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/gettest/:id")
                .consumes("text/plain")
                .produces("application/json")
                .blockingHandler(rc -> handleRequest(rc, "{\"accepted\":\"plain\"}"));

        router.get("/gettest/:id")
                .consumes("application/json")
                .produces("application/json")
                .blockingHandler(rc -> handleRequest(rc, "{\"accepted\":\"json\"}"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Constants.DEFAULT_PORT, res -> {
                    if (res.succeeded()){
                        fut.complete();
                    }
                    else{
                        fut.fail(res.cause());
                    }
                });

    }

    private void handleRequest(RoutingContext rc, String message){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rc.response().setStatusCode(200).end(message);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(BlockingRESTExampleVerticle.class.getName(),
                new DeploymentOptions().setWorker(true).setInstances(4),
                reply -> {
            DeploymentOptions opts = new DeploymentOptions().setWorker(true).setInstances(4);
            System.out.println("deploying client verticles");
            vertx.deployVerticle(HttpClientVerticle.class.getName(), opts, deployment -> {

                if(deployment.succeeded()){
                    System.out.println("client verticles deployed. Publishing messages");
                    vertx.eventBus().send(Constants.CALL_URL_ADDRESS, "/gettest/14" );
                    vertx.eventBus().send(Constants.CALL_URL_ADDRESS, "/gettest/14" );
                    vertx.eventBus().send(Constants.CALL_URL_ADDRESS, "/gettest/14" );
                    vertx.eventBus().send(Constants.CALL_URL_ADDRESS, "/gettest/14" );
                    System.out.println("Messages published");
                }
            });
        });
    }

}
