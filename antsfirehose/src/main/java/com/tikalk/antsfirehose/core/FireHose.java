package com.tikalk.antsfirehose.core;

import com.tikalk.antsfirehose.Constants;
import com.tikalk.antsfirehose.models.AntState;
import com.tikalk.antsfirehose.models.Game;
import com.tikalk.antsfirehose.models.GameState;

import java.util.Map;
import java.util.stream.Collectors;

public class FireHose {
    public static Game generate() {
        if (Game.currentGame == null) {
            return Game.currentGame;
        }
        Game currentGame = Game.currentGame;
        if (currentGame.getState() != GameState.STARTED) {
            return currentGame;
        }
        return advanceMove(currentGame);
    }

    private static Game advanceMove(Game currentGame) {
        Game.GameBuilder advancedGame = Game.builder()
                .id(currentGame.getId())
                .state(currentGame.getState())
                .teams(currentGame.getTeams())
                .finishSent(currentGame.isFinishSent());
        Map<Integer, AntState> newStates = currentGame.getAntStates().entrySet().stream()
                .filter(e -> e.getValue().getMovesSmashed() <= Constants.KEEP_SMASHED_MOVES)
                .filter(e -> e.getValue().isOnBoard())
                .map(FireHose::advanceAnt)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (newStates.size() == 0){
            advancedGame.state(GameState.FINISHED);
        }
        return advancedGame.antStates(newStates).build();
    }

    private static Map.Entry<Integer, AntState> advanceAnt(Map.Entry<Integer, AntState> antEntry) {
        AntState antState = antEntry.getValue();
        antState.setX(Math.ceil(antState.getDx() + antState.getX()));
        antState.setY(Math.ceil(antState.getDy() + antState.getY()));
        if (antState.getMovesSmashed() > 0) {
            antState.setMovesSmashed(antState.getMovesSmashed() + 1);
        }
        return antEntry;
    }
}
