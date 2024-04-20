package com.stepanew.goodmarksman.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PlayerInfo {

    String playerName;
    int shotCounter;
    int scoreCounter;

    public PlayerInfo(String playerName) {
        this.playerName = playerName;
    }

    public void incrementShots() {
        shotCounter++;
    }

    public void incrementScore(int score) {
        scoreCounter += score;
    }

    public void reset() {
        shotCounter = 0;
        scoreCounter = 0;
    }

}
