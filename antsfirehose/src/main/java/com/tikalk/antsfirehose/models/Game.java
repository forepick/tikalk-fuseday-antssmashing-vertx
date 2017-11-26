package com.tikalk.antsfirehose.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Game {
    private int id;
    private Map<String, Integer> teams;
    private GameState state;
    private Map<Integer, AntState> antStates;
    private boolean finishSent;

    public static Game currentGame;
}
