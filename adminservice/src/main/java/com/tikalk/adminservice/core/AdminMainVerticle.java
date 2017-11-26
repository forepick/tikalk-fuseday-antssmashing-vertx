package com.tikalk.adminservice.core;

import com.tikalk.adminservice.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;

public class AdminMainVerticle extends AbstractVerticle{


    private Vertx vertx = Vertx.vertx();


    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);

        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("X-PINGARUNER")
                .allowedHeader("Content-Type"));

        router.get("/games/latest").handler(this::getLatestGames);
        router.get("/antspecies").handler(this::getAntSpecies);
        router.get("/teams/current").handler(this::getCurrentTeams);

        router.post("/games").handler(this::addOne);
        router.put("/games/start").handler(this::startGame);


        router.route("/admin/*").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>");
        });

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", Constants.DEFAULT_PORT),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private void startGame(RoutingContext routingContext) {
        String name = routingContext.request().getParam("name");
        WebClient client = WebClient.create(vertx);
        client
                .get(Constants.FIREHOSE_PORT, Constants.FIREHOSE_SERVER, "/games/start")
                .send(ar -> {
                    if (ar.succeeded()) {
                        // Obtain response
                        HttpResponse<Buffer> response = ar.result();

                        routingContext.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .putHeader("Access-Control-Allow-Origin", "*")
                                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                                .setStatusCode(400).end();
                    } else {
                        routingContext.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .putHeader("Access-Control-Allow-Origin", "*")
                                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                                .setStatusCode(204).end();
                    }
                });


    }

    private void getCurrentTeams(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .end("[{\"id\":9,\"name\":\"teamA\",\"antSpecies\":{\"id\":1,\"name\":\"Red_Fire\"},\"score\":0},{\"id\":10,\"name\":\"teamB\",\"antSpecies\":{\"id\":2,\"name\":\"Lasius\"},\"score\":0},{\"id\":11,\"name\":\"teamA\",\"antSpecies\":{\"id\":3,\"name\":\"Mirmica\"},\"score\":0}]");
    }

    private void getAntSpecies(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .end("[{\"id\": 1,\"name\": \"Red_Fire\"},{\"id\": 2,\"name\": \"Lasius\"},{\"id\": 3,\"name\": \"Mirmica\"}]");
    }

    private void getLatestGames(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .end("{\"id\":8,\"createdAt\":1511442651,\"updatedAt\":1511442654,\"state\":\"STARTED\",\"startTime\":1511442654,\"stopTime\":null,\"pauseTime\":null,\"resumeTime\":null,\"finishTime\":null,\"timeFrameToRunInSec\":60}");
    }

    private void addOne(RoutingContext routingContext) {
//        final Game game = Json.decodeValue(routingContext.getBodyAsString(), Game.class);
        String gameTime = routingContext.request().getParam("gameTime");
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .end(Json.encodePrettily(gameTime));
    }
}
