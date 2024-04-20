package com.stepanew.goodmarksman.store;

import java.util.List;
import java.util.Optional;

public interface PlayerDAO {

    void addPlayer(PlayerEntity player);
    Optional<Integer> getPlayerWins(String name);
    void setPlayerWins(PlayerEntity player);
    void incrementPlayerWins(PlayerEntity player);
    List<PlayerEntity> getAllPlayers();

}
