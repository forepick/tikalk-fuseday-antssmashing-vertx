package com.tikalk.antsfirehose.models;

import com.tikalk.antsfirehose.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AntState {
    private int id;
    private String teamName;
    private double x;
    private double y;
    private double dx;
    private double dy;
    private double direction;
    private int speed;
    private int movesSmashed;

    public AntState(int id, String teamName, double x, double y, double direction, int speed) {
        this.id = id;
        this.teamName = teamName;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = direction;
        this.dx = Math.cos(direction) * speed;
        this.dy = Math.sin(direction) * speed;
        this.speed = Constants.ANT_SPEED;
        this.movesSmashed = 0;
    }

    public boolean isOnBoard() {
        return !(x < 0) && !(x > 1000) && !(y < 0) && !(y > 1000);
    }

}
