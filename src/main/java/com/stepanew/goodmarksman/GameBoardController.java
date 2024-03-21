package com.stepanew.goodmarksman;

import com.stepanew.goodmarksman.models.Model;
import javafx.application.Platform;
import javafx.fxml.FXML;
import lombok.extern.slf4j.Slf4j;
import javafx.scene.shape.Circle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class GameBoardController extends GameBoardView {

    final static double CIRCLE_LEFT_SPEED = 2.5;
    final static int FRAME = 16;
    final static double CIRCLE_RIGHT_SPEED = 5.0;
    final static double ARROW_SPEED = 4.0;
    final static int LEFT_CIRCLE_VALUE = 5;
    final static int RIGHT_CIRCLE_VALUE = 25;
    static boolean IS_ARROW_LAUNCHED = false;
    static boolean IS_GAME_STARTED = false;
    static short DIRECTION_LEFT = 1;
    static short DIRECTION_RIGHT = 1;

    Model model;
    ExecutorService threadPool = Executors.newFixedThreadPool(2);

/*    public GameBoardController() {
        this.model = new Model();
    }*/

    public void initialize(){
        this.model = new Model();
        addPlayerInfo(model.getPlayerInfo());
    }

    @FXML
    void startGame() {
        if (!IS_GAME_STARTED) {
            IS_GAME_STARTED = true;
            threadPool.submit(() -> {
                while (IS_GAME_STARTED) {
                    Platform.runLater(() -> {
                        moveCircles();
                        moveArrow();
                    });
                    try {
                        Thread.sleep(FRAME);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            });
        }
    }

    private void moveCircles() {
        clearCirclesPane();
        model.moveLeftCircle(CIRCLE_LEFT_SPEED * DIRECTION_LEFT);
        if (checkBorder(model.getLeftCircle())) {
            DIRECTION_LEFT *= -1;
        }

        model.moveRightCircle(CIRCLE_RIGHT_SPEED * DIRECTION_RIGHT);
        if (checkBorder(model.getRigthCircle())) {
            DIRECTION_RIGHT *= -1;
        }

        displayCircle(model.getLeftCircle());
        displayCircle(model.getRigthCircle());
    }

    private void moveArrow() {
        if (IS_ARROW_LAUNCHED) {
            clearArrowsPane(model.getArrow());
            model.moveArrow(ARROW_SPEED);
            displayArrow(model.getArrow());
            if (model.getArrowEndX() >= ARROWS_PANE_END || checkIntersection()) {
                resetArrow();
            }
        }
    }

    private void resetArrow() {
        clearArrowsPane(model.getArrow());
        model.resetArrowCoordinates();
        IS_ARROW_LAUNCHED = false;
    }


    private boolean checkBorder(Circle circle) {
        return circle.getCenterY() - circle.getRadius() <= CIRCLES_PANE_LOW_HEIGHT
                || circle.getCenterY() + circle.getRadius() >= CIRCLES_PANE_HIGH_HEIGHT;
    }

    @FXML
    void shoot() {
        if (!IS_ARROW_LAUNCHED) {
            incrementShots();
            IS_ARROW_LAUNCHED = true;
        }
    }

    @FXML
    void reset() {
        threadPool.shutdown();
        IS_ARROW_LAUNCHED = false;
        IS_GAME_STARTED = false;
        clearArrowsPane(model.getArrow());
        clearCirclesPane();
        resetScores();
        model = new Model();
        clearCirclesPane();
        threadPool = Executors.newFixedThreadPool(2);
    }

    private void incrementShots() {
        PlayerInfoController.setShotCounter(getPlayersInfo().get(0) ,model.incrementShotCounter());
    }

    private void incrementScore(int score) {
        PlayerInfoController.setScoreCounter(getPlayersInfo().get(0) ,model.incrementScoreCounter(score));
    }

    private boolean checkIntersection() {
        double distanceToLeft = Math.sqrt(
                Math.pow(model.getLeftCenterX() - model.getArrowEndX(), 2)
                        + Math.pow(model.getLeftCenterY() - model.getArrowEndY(), 2)
        );

        if (distanceToLeft <= model.getLEFT_RADIUS()) {
            incrementScore(LEFT_CIRCLE_VALUE);
            return true;
        }

        double distanceToRight = Math.sqrt(
                Math.pow(model.getRightCenterX() - model.getArrowEndX(), 2)
                        + Math.pow(model.getRightCenterY() - model.getArrowEndY(), 2)
        );

        if (distanceToRight <= model.getRIGHT_RADIUS()) {
            incrementScore(RIGHT_CIRCLE_VALUE);
            return true;
        }

        return false;
    }

    private void clearCirclesPane() {
        super.clearCirclesPane(model.getLeftCircle());
        super.clearCirclesPane(model.getRigthCircle());
    }

    private void resetScores() {
        PlayerInfoController.setShotCounter(getPlayersInfo().get(0), 0);
        PlayerInfoController.setScoreCounter(getPlayersInfo().get(0) , 0);
    }

}
