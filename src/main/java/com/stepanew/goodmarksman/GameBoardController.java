package com.stepanew.goodmarksman;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameBoardController extends GameBoardView{

    final static double CIRCLE_LEFT_SPEED = 2.5;
    final static int FRAME = 16;
    final static double CIRCLE_RIGHT_SPEED = 5.0;
    final static double ARROW_SPEED = 4.0;
    final static int LEFT_CIRCLE_VALUE = 5;
    final static int RIGHT_CIRCLE_VALUE = 25;
    static boolean IS_ARROW_LAUNCHED = false;
    static short DIRECTION_LEFT = 1;
    static short DIRECTION_RIGHT = 1;
    static boolean IS_GAME_STARTED = false;

    Model model;
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    Future<?> arrowTask;
    Future<?> circleTask;

    @FXML
    Button startGameButton;

    @FXML
    Button pauseButton;

    @FXML
    Button shootButton;

    @FXML
    Button resetGameButton;

    public GameBoardController() {
        this.model = new Model();
    }

    @FXML
    void startGame() {
        if (!IS_GAME_STARTED) {
            IS_GAME_STARTED = true;
            circleTask = threadPool.submit(() -> {
                while (IS_GAME_STARTED) {
                    Platform.runLater(() -> {
                        moveCircles();
                    });
                    try {
                        Thread.sleep(FRAME);
                    } catch (InterruptedException e) {
                        Thread.currentThread().isInterrupted();
                    }
                }
            });
        }
    }

    private void moveCircles() {
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
    }


    private boolean checkBorder(Circle circle){
        return circle.getCenterY() - circle.getRadius() <= CIRCLES_PANE_LOW_HEIGHT
                || circle.getCenterY() + circle.getRadius() >= CIRCLES_PANE_HIGH_HEIGHT;
    }


    @FXML
    void pause(){
        IS_GAME_STARTED = false;
    }

    @FXML
    void shoot() {
        if (IS_GAME_STARTED && !IS_ARROW_LAUNCHED) {
            IS_ARROW_LAUNCHED = true;
            incrementShots();
            arrowTask = threadPool.submit(() -> {
                while (model.getArrowEndX() < ARROWS_PANE_END) {
                    Platform.runLater(() -> {
                        checkIntersection();
                        clearArrowsPane();
                        model.moveArrow(ARROW_SPEED);
                        displayArrow(model.getArrow());
                    });

                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    try {
                        Thread.sleep(FRAME);
                    } catch (InterruptedException e) {
                        Thread.currentThread().isInterrupted();
                        return;
                    }
                }

                Platform.runLater(this::clearArrowsPane);
                model.resetArrowCoordinates();
                IS_ARROW_LAUNCHED = false;
            });
        }
    }

    @FXML
    void reset() {
        Thread resetThread = new Thread(this::resetGame);
        resetThread.setDaemon(true);
        resetThread.start();
    }

    private void resetGame() {
        if (arrowTask != null && !arrowTask.isDone()) {
            arrowTask.cancel(true);
        }
        threadPool.shutdown();
        IS_GAME_STARTED = false;
        IS_ARROW_LAUNCHED = false;
        model = new Model();
        threadPool = Executors.newFixedThreadPool(10);
        Platform.runLater(() -> {
            clearArrowsPane();
            clearCirclesPane();
            resetScores();
        });
    }

    private void incrementShots() {
        displayShotLabel(model.incrementShotCounter());
    }

    private void incrementScore(int score){
        displayScoreLabel(model.incrementScoreCounter(score));
    }

    private boolean checkIntersection(){
        double distanceToLeft = Math.sqrt(
                Math.pow(model.getLeftCenterX() - model.getArrowEndX(), 2)
                        + Math.pow(model.getLeftCenterY() - model.getArrowEndY(), 2)
        );

        if(distanceToLeft <= model.getLEFT_RADIUS()){
            System.out.println("left circle x, y: " + model.getLeftCenterX() + ", " + model.getLeftCenterY() + " line x, y: " + model.getArrowEndX() + ", " + model.getArrowEndY());
            //incrementScore(LEFT_CIRCLE_VALUE);
            return true;
        }

        double distanceToRight = Math.sqrt(
                Math.pow(model.getRightCenterX() - model.getArrowEndX(), 2)
                        + Math.pow(model.getRightCenterY() - model.getArrowEndY(), 2)
        );

        if(distanceToRight <= model.getRIGHT_RADIUS()){
            System.out.println("right circle x, y: " + model.getRightCenterX() + ", " + model.getRightCenterY() + " line x, y: " + model.getArrowEndX() + ", " + model.getArrowEndY());
            //incrementScore(RIGHT_CIRCLE_VALUE);
            return true;
        }

        return false;
    }

}
