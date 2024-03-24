package com.stepanew.goodmarksman;

import com.stepanew.goodmarksman.models.Model;
import com.stepanew.goodmarksman.models.ModelBuilder;
import com.stepanew.goodmarksman.models.PlayerInfo;
import com.stepanew.goodmarksman.server.IObserver;
import com.stepanew.goodmarksman.server.SocketMessageWrapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import javafx.scene.shape.Circle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class GameBoardController extends GameBoardView implements IObserver {

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
    SocketMessageWrapper socketMessageWrapper;
    String playerName;
    List<Button> players;
    List<VBox> playersInfo;
    List<Circle> targets;
    {
        players = new ArrayList<>();
        playersInfo = new ArrayList<>();
        targets = new ArrayList<>();
    }


    public void initialize(){
        this.model = ModelBuilder.build();
        model.addObserver(this);
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

    public void dataInit(SocketMessageWrapper socketMessageWrapper, String playersName) {
        this.socketMessageWrapper = socketMessageWrapper;
        this.playerName = playersName;
    }

    @Override
    public void update() {
        checkWinner();
        updateCircles(model.getTargetList());
        updatePlayerInfo(model.getPlayerList());
        System.out.println("-----------" + model.getPlayerList().size());
        updatePlayers(model.getPlayerList());
    }

    private void updatePlayers(List<PlayerInfo> playerList) {
        if (playerList == null || playerList.size() == 0 || players.size() == playerList.size()) {
            return;
        }
        Platform.runLater(() -> {
            for(int i = 0; i < playerList.size(); i++) {
                if (i >= players.size()) {
                    Button button = new Button();
                    button.setPrefHeight(140);
                    button.setPrefWidth(140); //TODO вынести в константу

                    if (playerList.get(i).getPlayerName().equals(playerName)) {
                        button.getStyleClass().add("player-client");
                        button.setText("Вы");
                    } else {
                        button.getStyleClass().add("player-connect");
                    }

                    players.add(button);
                    addPlayersBox(button);
                }
            }
        });
    }

    private void updatePlayerInfo(List<PlayerInfo> playerList) {
        if(playerList == null || playerList.size() == 0) {
            System.out.println(playerName + "Bad time");
            return;
        }
        Platform.runLater(() -> {
            System.out.println("in");
            for (int i = 0; i < playerList.size(); i++) {
                if (i >= players.size()) {
                    VBox vBox = PlayerInfoController.createPlayerInfoVBox(playerList.get(i));
                    playersInfo.add(vBox);
                    System.out.println("New vbox");
                    addPlayerInfo(vBox);
                } else {
                    PlayerInfoController.setPlayerName(playersInfo.get(i), playerList.get(i).getPlayerName());
                    PlayerInfoController.setShotCounter(playersInfo.get(i), playerList.get(i).getShotCounter());
                    PlayerInfoController.setScoreCounter(playersInfo.get(i), playerList.get(i).getScoreCounter());
                }
            }
        });

    }

    private void updateCircles(List<Circle> targetList) {
        if (targetList == null || targetList.size() == 0) {
            return;
        }
        Platform.runLater(() -> {
            for(int i = 0; i < targetList.size(); i++) {
                if (i >= targets.size()) {
                    Circle circle = targetList.get(i);
                    circle.getStyleClass().add("targets");
                    targets.add(circle);
                }
            }
        });
    }

    private void checkWinner() {
        if(model.getWinner() != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("У нас есть победитель!");
                alert.setHeaderText("У нас есть победитель!");
                alert.setContentText("Победитель : " + ((model.getWinner()).equals(this.playerName) ? "Вы" : model.getWinner()) + "!");
                alert.showAndWait();  
            });
        }
    }
}
