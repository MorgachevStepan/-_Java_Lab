package com.stepanew.goodmarksman.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerInfo {

    String playerName;
    int shotCounter;
    int scoreCounter;

    public PlayerInfo(String playerName) {
        this.playerName = playerName;
    }

}
