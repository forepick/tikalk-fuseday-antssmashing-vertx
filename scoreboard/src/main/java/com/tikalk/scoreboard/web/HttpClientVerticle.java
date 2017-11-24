package com.tikalk.scoreboard.web;

import com.tikalk.scoreboard.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;

public class HttpClientVerticle extends AbstractVerticle {

    private EventBus eb;
    private HttpClient client;

    @Override
    public void start(Future<Void> fut) throws Exception {
        System.out.printf("%s: deployed\n", displayName());

        eb = vertx.eventBus();
        client = vertx.createHttpClient();
        eb.consumer("call.url", message -> {
            System.out.printf("%s: got message! sleeping...\n", displayName());
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s: requesting URL\n", deploymentID());
            client.get(Constants.DEFAULT_PORT, "localhost", message.body().toString(), reply -> {
                System.out.printf("%s: %s\n", displayName(), "reply");
                message.reply("ok");
            }).putHeader("Content-Type", "application/json").end();
        });

        fut.complete();
    }
    private String displayName(){
        return String.format("d %s, t %s", deploymentID(), Thread.currentThread().getName());
    }
}
