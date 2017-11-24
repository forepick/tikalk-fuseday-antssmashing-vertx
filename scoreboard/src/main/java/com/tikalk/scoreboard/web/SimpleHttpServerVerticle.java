package com.tikalk.scoreboard.web;

import com.tikalk.scoreboard.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class SimpleHttpServerVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> fut) throws Exception {
        vertx
                .createHttpServer()
                .requestHandler(r -> {
                    r.response().end("<h1>Hello Tikal</h1>");
                })
                .listen(Constants.DEFAULT_PORT, result ->
                {
                    if (result.succeeded()){
                        fut.complete();
                    }
                    else{
                        fut.fail(result.cause());
                    }
                });
    }
}
