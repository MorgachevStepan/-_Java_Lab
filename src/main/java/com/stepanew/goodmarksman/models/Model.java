package com.stepanew.goodmarksman.models;

import com.stepanew.goodmarksman.GameBoardController;
import com.stepanew.goodmarksman.server.IObserver;
import com.stepanew.goodmarksman.server.Server;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Model {

    Line arrow;
    Circle leftCircle;
    Circle rigthCircle;
    PlayerInfo playerInfo;
    String winner;

    List<Point> targetList;
    List<Point> arrowList;
    List<PlayerInfo> playerList;
    List<String> readyList;
    List<String> waitingList;
    List<String> shootingList;
    List<IObserver> observerList;

    volatile boolean IS_GAME_RESET = true;


    final double ARROW_LENGTH = 60;
    final double ARROW_WIDTH = 5;
    final double ARROW_Y = 154;
    final double Y_BOUND = 520;
    final double ARROW_X_START = 40;
    final double LEFT_X = 300;
    final double CIRCLE_Y = 260;
    final double LEFT_RADIUS = 60;
    final double RIGHT_RADIUS = 30;
    final double RIGHT_X = 350;

    {
        winner = null;
        playerInfo = null;
        targetList = new ArrayList<>();
        arrowList = new ArrayList<>();
        playerList = new ArrayList<>();
        readyList = new ArrayList<>();
        shootingList = new ArrayList<>();
        waitingList = new ArrayList<>();
        observerList = new ArrayList<>();
    }

    public void initialize() {
        targetList.add(new Point(LEFT_X, CIRCLE_Y, LEFT_RADIUS));
        targetList.add(new Point(RIGHT_X, CIRCLE_Y, RIGHT_RADIUS));
        updateArrowsPosition();
    }

    private synchronized void updateArrowsPosition() {
        arrowList.clear();
        int clientsCounter = playerList.size();
        for (int i = 1; i <= clientsCounter; i++) {
            double step = Y_BOUND / (clientsCounter + 1);
            arrowList.add(new Point(ARROW_X_START, step * i, ARROW_LENGTH));
        }
    }

    public double getLeftCenterY() {
        return leftCircle.getCenterY();
    }

    public double getRightCenterY() {
        return rigthCircle.getCenterY();
    }

    public double getLeftCenterX() {
        return leftCircle.getCenterX();
    }

    public double getRightCenterX() {
        return rigthCircle.getCenterX();
    }

    public double getArrowEndX() {
        return arrow.getEndX();
    }

    public double getArrowEndY() {
        return arrow.getEndY();
    }

    public void moveArrow(double deltaX) {
        arrow.setStartX(arrow.getStartX() + deltaX);
        arrow.setEndX(arrow.getEndX() + deltaX);
    }

    public void moveLeftCircle(double deltaY) {
        leftCircle.setCenterY(leftCircle.getCenterY() - deltaY);
    }

    public void moveRightCircle(double deltaY){
        rigthCircle.setCenterY(rigthCircle.getCenterY() - deltaY);
    }

    public void resetArrowCoordinates() {
        arrow.setStartX(ARROW_X_START);
        arrow.setEndX(ARROW_X_START + ARROW_LENGTH);
    }

    public int incrementShotCounter() {
        playerInfo.setShotCounter(playerInfo.getShotCounter() + 1);
        return playerInfo.getShotCounter();
    }

    public int incrementScoreCounter(int score) {
        playerInfo.setScoreCounter(playerInfo.getScoreCounter() + score);
        return playerInfo.getScoreCounter();
    }

    public void addPlayer(PlayerInfo playerInfo) {
        playerList.add(playerInfo);
        this.updateArrowsPosition();
    }

    public void ready(Server server, String playerName) {
        if(readyList.isEmpty()) {
            readyList.add(playerName);
            return;
        }

        if(readyList.contains(playerName)) {
            readyList.remove(playerName);
        } else {
            readyList.add(playerName);
        }

        if(playerList.size() > 1 && readyList.size() == playerList.size()) {
            IS_GAME_RESET = false;
            startGame(server);
        }
    }

    private void startGame(Server server) {
        Thread thread = new Thread(
                () -> {
                    while (true) {
                        server.broadcast();
                    }
                }
        );
        thread.start();
    }

    public void requestShoot(String playerName) {
        if (IS_GAME_RESET) {
            return;
        }

        PlayerInfo info = playerList.stream()
                .filter(clientData -> clientData.getPlayerName().equals(playerName))
                .findFirst()
                .orElse(null);
        assert info != null;

        if(!shootingList.contains(info.getPlayerName())) {
            shootingList.add(info.getPlayerName());
            info.incrementShots();
        }
    }

    public void requestStop(String playerName) {
        if(IS_GAME_RESET) {
            return;
        }

        if(waitingList.contains(playerName)) {
            waitingList.remove(playerName);
            if (waitingList.size() == 0) {
                synchronized (this) {
                    notifyAll();
                }
            }
        } else {
            waitingList.add(playerName);
        }
    }

    public void update() {
        for(IObserver observer: observerList) {
            observer.update();
        }
    }

    public void addObserver(GameBoardController gameBoardController) {
        observerList.add(gameBoardController);
    }
}
