package com.tikalk.antsfirehose.web.example;

import com.tikalk.antsfirehose.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class NonblockingHandlerVerticle extends AbstractVerticle {

    @Override

    public void start(Future<Void> fut) throws Exception {
        vertx.eventBus().consumer(Constants.PROCESS_REQUEST_ADDRESS, message -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message.reply(message.body().toString());
        });
        fut.complete();
    }
}
