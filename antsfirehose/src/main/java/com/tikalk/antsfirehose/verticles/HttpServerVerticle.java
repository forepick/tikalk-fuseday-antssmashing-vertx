package com.tikalk.antsfirehose.verticles;

import com.tikalk.antsfirehose.Constants;
import com.tikalk.antsfirehose.models.AntState;
import com.tikalk.antsfirehose.models.Game;
import com.tikalk.antsfirehose.models.GameState;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HttpServerVerticle extends AbstractVerticle {
    final private static Logger LOGGER = LogManager.getLogger(HttpServerVerticle.class);
    final private Random rand = new Random();

    @Override
    public void start(Future<Void> fut) throws Exception {
        LOGGER.info("Starting HttpServerVerticle");
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/health").handler(r -> {
            r.response().end("OK");
        });
        router.put("/game").handler(this::createGame);
        router.get("/game/:gameId/:changeTo").handler(this::changeGameState);
        router.get("/ant/kill/:antId").handler(this::killAnt);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Constants.DEFAULT_PORT);
        LOGGER.info("HttpServerVerticle Started");
    }

    private void createGame(RoutingContext routingContext) {
        long start = System.currentTimeMillis();
        HttpServerResponse response = routingContext.response();
        JsonObject newGameJson = null;
        try {
            newGameJson = routingContext.getBodyAsJson();
        } catch (Exception e) {
            sendError(400, "Empty body", response);
            return;
        }
        if (newGameJson == null) {
            sendError(400, "Empty new game request", response);
            return;
        }
        int gameId = newGameJson.getInteger("id", 0);
        if (gameId == 0) {
            sendError(400, "No gameId was provided", response);
            return;
        }
        JsonArray teams = newGameJson.getJsonArray("teams");
        if (teams == null || teams.size() < 2) {
            sendError(400, "No teams were passed", response);
            return;
        }
        Map<String, Integer> teamsMap = new HashMap<>();
        for (Object team : teams) {
            JsonObject teamJson = (JsonObject) team;
            teamsMap.put(teamJson.getString("name"), teamJson.getInteger("antSpeciesId"));
        }
        Map<Integer, AntState> newAntStates = new HashMap<>();
        int nextId = 1;
        for (String name : teamsMap.keySet()) {
            for (int i = 0; i < Constants.ANTS_PER_TEAM; i++) {
                AntState newAnt = new AntState(nextId++, name,
                        Math.ceil(rand.nextFloat() * 1000),
                        Math.ceil(rand.nextFloat() * 1000),
                        rand.nextFloat() * 2 * Math.PI, Constants.ANT_SPEED);
                newAntStates.put(newAnt.getId(), newAnt);
            }
        }
        Game.currentGame = new Game(gameId, teamsMap, GameState.CREATED, newAntStates, false);
        response.end("Game created");
        LOGGER.info("Finished creating game. Took: {}ms", System.currentTimeMillis() - start);

    }

    private void changeGameState(RoutingContext routingContext) {
        long start = System.currentTimeMillis();
        String gameId = routingContext.request().getParam("gameId");
        GameState newState = GameState.fromCommand(routingContext.request().getParam("changeTo"));
        LOGGER.info("Changing game state: {}/{}", gameId, newState);
        HttpServerResponse response = routingContext.response();
        if (gameId == null || newState == null) {
            sendError(400, "Missing gameId or changeTo", response);
            return;
        }
        if (Game.currentGame == null) {
            sendError(400, "No currently running game, cannot change state", response);
            return;
        }
        if (newState == GameState.CREATED) {
            sendError(400, "Created is not a legal state for this Endpoint", response);
            return;
        }
        if (newState == Game.currentGame.getState()) {
            sendError(400, "New status is same as previous. Nothing happened", response);
        }
        Game.currentGame.setState(newState);
        response.end("State changed");
        LOGGER.info("Finished Changing game. Took: {}ms", System.currentTimeMillis() - start);
    }

    private void killAnt(RoutingContext routingContext) {
        long start = System.currentTimeMillis();
        String antIdStr = routingContext.request().getParam("antId");
        HttpServerResponse response = routingContext.response();
        int antId = 0;
        try {
            antId = Integer.valueOf(antIdStr);
        } catch (NumberFormatException e) {
            sendError(400, "AntId is not a nubmer", response);
            return;
        }
        LOGGER.info("Smashing ant state: {}", antId);
        String msg;
        if (antId == 0) {
            msg = "Missing antId";
            sendError(400, msg, response);
            return;
        } else if (Game.currentGame == null) {
            msg = "No currently running game, cannot change state";
            sendError(400, msg, response);
            return;
        }
        AntState antState = Game.currentGame.getAntStates().get(antId);
        if (antState == null) {
            msg = "Such ant doesn't exist";
            sendError(400, msg, response);
            return;
        }
        antState.setMovesSmashed(1);
        msg = "Smashed Ant " + antId;
        response.end(msg);
        LOGGER.info("Finished Smashing ant. Took: {}ms", System.currentTimeMillis() - start);
    }

    private void sendError(int statusCode, String errorMsg, HttpServerResponse response) {
        String message = "{ \"status\":" + statusCode + ", \"error\": \"" + errorMsg + "\"}";
        response.setStatusCode(statusCode).end(message);
    }
}
