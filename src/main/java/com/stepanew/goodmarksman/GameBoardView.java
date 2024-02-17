package com.stepanew.goodmarksman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameBoardView {

    final static double CIRCLES_PANE_HIGH_HEIGHT = 330.0;
    final static double CIRCLES_PANE_LOW_HEIGHT = 20.0;

    @FXML
    Label playerScoreLabel;

    @FXML
    Label shotLabel;

    @FXML
    Pane circlesPane;

    public void displayCircle(Circle circle){
        circlesPane.getChildren().add(circle);
    }

    public void clearCirclesPane(){
        circlesPane.getChildren().clear();
    }

}
