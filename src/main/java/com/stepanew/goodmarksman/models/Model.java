package com.stepanew.goodmarksman.models;

import com.stepanew.goodmarksman.GameBoardController;
import com.stepanew.goodmarksman.server.IObserver;
import com.stepanew.goodmarksman.server.Server;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Model {

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
    final double LEFT_X = 400;
    final double CIRCLE_Y = 260;
    final double LEFT_RADIUS = 60;
    final double RIGHT_RADIUS = 30;
    final double RIGHT_X = 450;
    final double CIRCLE_LEFT_SPEED = 2.5;
    final int FRAME = 16;
    final double CIRCLE_RIGHT_SPEED = 5.0;
    final double ARROW_SPEED = 4.0;
    final int LEFT_CIRCLE_VALUE = 5;
    final int RIGHT_CIRCLE_VALUE = 25;
    final int WINNER_SCORE = 50;
    short DIRECTION_LEFT = 1;
    short DIRECTION_RIGHT = 1;
    final double ARROWS_PANE_END = 400.0;

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

    public void moveLeftCircle(double deltaY) {
        targetList.get(0).setYCoordinate(targetList.get(0).getYCoordinate() - deltaY);
    }

    public void moveRightCircle(double deltaY) {
        targetList.get(1).setYCoordinate(targetList.get(1).getYCoordinate() - deltaY);
    }

    public void addPlayer(PlayerInfo playerInfo) {
        playerList.add(playerInfo);
        this.updateArrowsPosition();
    }

    public void ready(Server server, String playerName) {
        if (readyList.isEmpty()) {
            readyList.add(playerName);
            return;
        }

        if (readyList.contains(playerName)) {
            readyList.remove(playerName);
        } else {
            readyList.add(playerName);
        }

        if (playerList.size() > 1 && readyList.size() == playerList.size()) {
            IS_GAME_RESET = false;
            startGame(server);
        }
    }

    private void startGame(Server server) {
        Thread thread = new Thread(
                () -> {
                    while (true) {
                        if (IS_GAME_RESET) {
                            winner = null;
                            break;
                        }
                        if (waitingList.size() != 0) {
                            synchronized (this) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        if (shootingList.size() != 0) {
                            shoot();
                        }
                        moveCircles();

                        server.broadcast();

                        try {
                            Thread.sleep(FRAME);
                        } catch (InterruptedException ignored) {

                        }
                    }
                }
        );
        thread.start();
    }

    private synchronized void shoot() {
        for (int i = 0; i < shootingList.size(); i++) {
            if (shootingList.get(i) == null) {
                break;
            }
            int finalI = i;
            PlayerInfo player = playerList.stream()
                    .filter(
                            data -> data.getPlayerName()
                                    .equals(
                                            shootingList.get(finalI)
                                    )
                    ).findFirst()
                    .orElse(null);
            int index = playerList.indexOf(player);
            Point point = arrowList.get(index);
            point.setXCoordinate(point.getXCoordinate() + ARROW_SPEED);
            manageShot(point, player);
        }
    }

    private void moveCircles() {
        Point leftCircle = targetList.get(0);
        Point rightCircle = targetList.get(1);

        if (checkBorder(rightCircle)) {
            DIRECTION_RIGHT *= -1;
        }
        moveRightCircle(CIRCLE_RIGHT_SPEED * DIRECTION_RIGHT);

        if (checkBorder(leftCircle)) {
            DIRECTION_LEFT *= -1;
        }

        moveLeftCircle(CIRCLE_LEFT_SPEED * DIRECTION_LEFT);
    }

    private boolean checkBorder(Point circle) {
        return circle.getYCoordinate() <= circle.getRadius()
                || Y_BOUND - circle.getYCoordinate() <= circle.getRadius();
    }

    private synchronized void manageShot(Point point, PlayerInfo player) {
        ShotState shotState = checkHit(point);

        switch (shotState) {
            case FLYING -> {
                return;
            }
            case BIG_SHOT -> player.incrementScore(LEFT_CIRCLE_VALUE);
            case SMALL_SHOT -> player.incrementScore(RIGHT_CIRCLE_VALUE);
        }
        point.setXCoordinate(ARROW_X_START);
        if (shootingList.size() == 1) {
            shootingList.clear();
        } else {
            shootingList.remove(player.getPlayerName());
        }
        checkWinner();
    }

    private void checkWinner() {
        playerList.forEach(dataManager -> {
            if (dataManager.getScoreCounter() >= WINNER_SCORE) {
                this.winner = dataManager.getPlayerName();
                gameReset();
            }
        });
    }

    private void gameReset() {
        IS_GAME_RESET = true;
        readyList.clear();
        targetList.clear();
        arrowList.clear();
        waitingList.clear();
        shootingList.clear();
        playerList.forEach(PlayerInfo::reset);
        this.initialize();
    }

    private synchronized ShotState checkHit(Point point) {
        if (contains(targetList.get(1), point.getXCoordinate() + point.getRadius(), point.getYCoordinate())) {
            return ShotState.SMALL_SHOT;
        }
        if (contains(targetList.get(0), point.getXCoordinate() + point.getRadius(), point.getYCoordinate())) {
            return ShotState.BIG_SHOT;
        }
        if (point.getXCoordinate() > ARROWS_PANE_END) {
            return ShotState.MISSED;
        }
        return ShotState.FLYING;
    }

    private boolean contains(Point point, double x, double y) {
        return (
                Math.sqrt(
                        Math.pow(
                                (x - point.getXCoordinate()), 2
                        ) +
                                Math.pow(
                                        (y - point.getYCoordinate()), 2
                                )
                ) < point.getRadius()
        );
    }

    public void requestShoot(String playerName) {
        if (IS_GAME_RESET) {
            return;
        }

        PlayerInfo info = playerList.stream()
                .filter(clientData -> clientData.getPlayerName().equals(playerName))
                .findFirst()
                .orElseThrow();

        if (!shootingList.contains(info.getPlayerName())) {
            shootingList.add(info.getPlayerName());
            info.incrementShots();
        }
    }

    public void requestStop(String playerName) {
        if (IS_GAME_RESET) {
            return;
        }

        if (waitingList.contains(playerName)) {
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
        for (IObserver observer : observerList) {
            observer.update();
        }
    }

    public void addObserver(GameBoardController gameBoardController) {
        observerList.add(gameBoardController);
    }
}
