package com.stepanew.goodmarksman.models;

import com.stepanew.goodmarksman.store.PlayerDAOBuilder;

public class ModelBuilder {

    static Model model = new Model(PlayerDAOBuilder.build());

    static public Model build() {
        return model;
    }

}
