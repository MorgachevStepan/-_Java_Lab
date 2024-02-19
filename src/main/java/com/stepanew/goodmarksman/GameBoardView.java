package com.stepanew.goodmarksman;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameBoardView {

    final static double CIRCLES_PANE_HIGH_HEIGHT = 330.0;
    final static double CIRCLES_PANE_LOW_HEIGHT = 20.0;
    final static double ARROWS_PANE_END = 400.0;

    @FXML
    Label playerScoreLabel;

    @FXML
    Label shotLabel;

    @FXML
    Pane circlesPane;

    @FXML
    Pane arrowsPane;

    public void displayCircle(Circle circle){
        circlesPane.getChildren().add(circle);
    }

    public void displayArrow(Line arrow){
        arrowsPane.getChildren().add(arrow);
    }

    public void clearCirclesPane(){
        circlesPane.getChildren().clear();
    }

    public void clearArrowsPane(){
        arrowsPane.getChildren().clear();
    }

    public void displayShotLabel(int shotCounter){
        shotLabel.setText(Integer.toString(shotCounter));
    }

    public void displayScoreLabel(int scoreCounter){
        playerScoreLabel.setText(Integer.toString(scoreCounter));
    }

    public void resetScores(){
        playerScoreLabel.setText("0");
        shotLabel.setText("0");
    }

}
