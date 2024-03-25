package com.stepanew.goodmarksman;

import com.google.gson.Gson;
import com.stepanew.goodmarksman.models.Model;
import com.stepanew.goodmarksman.models.ModelBuilder;
import com.stepanew.goodmarksman.models.PlayerInfo;
import com.stepanew.goodmarksman.models.Point;
import com.stepanew.goodmarksman.server.IObserver;
import com.stepanew.goodmarksman.server.SocketMessageWrapper;
import com.stepanew.goodmarksman.server.response.ClientActions;
import com.stepanew.goodmarksman.server.response.ClientRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
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

    final Model model;
    String playerName;
    SocketMessageWrapper socketMessageWrapper;
    final Gson gson;
    final List<Button> players;
    final List<VBox> playersInfo;
    final List<Circle> targets;
    final List<Line> arrows;

    {
        players = new ArrayList<>();
        playersInfo = new ArrayList<>();
        targets = new ArrayList<>();
        arrows = new ArrayList<>();
        model = ModelBuilder.build();
        gson = new Gson();
    }


    public void initialize(){
        model.addObserver(this);
    }

    @FXML
    void ready() {
        /*if (!IS_GAME_STARTED) {
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
        }*/
        sendRequest(new ClientRequest(ClientActions.READY));
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

    @FXML
    void shoot() {
        sendRequest(new ClientRequest(ClientActions.SHOOT));
    }

    @FXML
    void pause() {
        sendRequest(new ClientRequest(ClientActions.STOP));
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

    public void dataInit(SocketMessageWrapper socketMessageWrapper, String playersName) {
        this.socketMessageWrapper = socketMessageWrapper;
        this.playerName = playersName;
    }

    private void sendRequest(ClientRequest request) {
        socketMessageWrapper.writeData(gson.toJson(request));
    }

    @Override
    public void update() {
        checkWinner();
        updateCircles(model.getTargetList());
        updatePlayerInfo(model.getPlayerList());
        updatePlayers(model.getPlayerList());
        updateArrows(model.getArrowList());
    }

    private void updateArrows(List<Point> arrowList) {
        if (arrowList == null || arrowList.size() == 0) {
            return;
        }
        Platform.runLater(() -> {
            arrows.forEach(this::clearArrowsPane);
            for (Point point : arrowList) {
                Line arrow = new Line(
                        point.getXCoordinate(),
                        point.getYCoordinate(),
                        point.getXCoordinate() + point.getRadius(),
                        point.getYCoordinate()
                );
                arrows.add(arrow);
                displayArrow(arrow);
            }
        });
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

    private void updateCircles(List<Point> targetList) {
        if (targetList == null || targetList.size() == 0) {
            return;
        }
        Platform.runLater(() -> {
            for(int i = 0; i < targetList.size(); i++) {
                if (i >= targets.size()) {
                    Point point = targetList.get(i);
                    Circle circle = new Circle(point.getXCoordinate(), point.getYCoordinate(), point.getRadius());
                    circle.getStyleClass().add("targets");
                    targets.add(circle);
                    displayCircle(circle);
                } else if (targetList.size() > targets.size()) {
                    for (int j = 0; j < targetList.size() - targets.size(); j++) {
                        targets.remove(targets.size() - 1);
                        removeCircle();
                    }
                } else {
                    targets.get(i).setCenterY(targetList.get(i).getYCoordinate());
                    targets.get(i).setCenterX(targetList.get(i).getXCoordinate());
                    targets.get(i).setRadius(targetList.get(i).getRadius());
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
