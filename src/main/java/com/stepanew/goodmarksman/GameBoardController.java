package com.stepanew.goodmarksman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameBoardController extends GameBoardView{

    final static double CIRCLE_LEFT_SPEED = 2.5;
    final static double CIRCLE_RIGHT_SPEED = 5.0;
    static short DIRECTION_LEFT = 1;
    static short DIRECTION_RIGHT = 1;
    static boolean IS_GAME_STARTED = false;

    final Model model;

    Timeline animation;

    @FXML
    Button startGameButton;

    @FXML
    Button pauseButton;

    @FXML
    Button shootButton;

    public GameBoardController() {
        this.model = new Model();
    }

    @FXML
    void startGame() {
        if(!IS_GAME_STARTED) {
            IS_GAME_STARTED = true;
            animation = new Timeline(new KeyFrame(Duration.millis(16), e -> {
                clearCirclesPane();
                model.setLeftCenterY(model.getLeftCenterY() - CIRCLE_LEFT_SPEED * DIRECTION_LEFT);
                if (checkBorder(model.getLeftCircle())) {
                    DIRECTION_LEFT *= -1;
                }

                model.setRightCenterY(model.getRightCenterY() - CIRCLE_RIGHT_SPEED * DIRECTION_RIGHT);
                if (checkBorder(model.getRigthCircle())) {
                    DIRECTION_RIGHT *= -1;
                }

                displayCircle(model.getLeftCircle());
                displayCircle(model.getRigthCircle());
            }));
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play();
        }
    }

    private boolean checkBorder(Circle circle){
        return circle.getCenterY() - circle.getRadius() <= CIRCLES_PANE_LOW_HEIGHT
                || circle.getCenterY() + circle.getRadius() >= CIRCLES_PANE_HIGH_HEIGHT;
    }


    @FXML
    void pause(){
        IS_GAME_STARTED = false;
        animation.pause();
    }

    @FXML
    void shoot(){

    }


}
