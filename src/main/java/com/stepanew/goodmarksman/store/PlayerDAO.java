package com.stepanew.goodmarksman.store;

import java.util.List;

public interface PlayerDAO {

    void addPlayer(PlayerEntity player);
    void updatePlayer(PlayerEntity player);
    List<PlayerEntity> getAllPlayers();

}
