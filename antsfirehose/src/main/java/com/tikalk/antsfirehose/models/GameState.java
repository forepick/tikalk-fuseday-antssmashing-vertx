package com.tikalk.antsfirehose.models;

import java.util.HashMap;
import java.util.Map;

public enum GameState {
    CREATED("create"),
    STARTED("start"),
    PAUSED("pause"),
    STOPPED("stop"),
    FINISHED("finish");

    private static Map<String, GameState> reverseMap;

    static {
        reverseMap = new HashMap<>();
        for (GameState gameState : GameState.values()) {
            reverseMap.put(gameState.command(), gameState);
        }
    }

    private String command;

    GameState(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }

    public static GameState fromCommand(String command) {
        return reverseMap.get(command);
    }

}
