package com.stepanew.goodmarksman.models;

import com.stepanew.goodmarksman.server.Server;
import javafx.scene.paint.Color;
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

    List<Circle> targetList;
    List<Line> arrowList;
    List<PlayerInfo> playerList;


    final double ARROW_LENGTH = 60;
    final double ARROW_WIDTH = 5;
    final double ARROW_Y = 154;
    final double Y_BOUND = 310;
    final double ARROW_X_START = -27;
    final double LEFT_X = 300;
    final double CIRCLE_Y = 120;
    final double LEFT_RADIUS = 60;
    final double RIGHT_RADIUS = 30;
    final double RIGHT_X = 350;


    public Model() {
        this.leftCircle = new Circle(LEFT_X, CIRCLE_Y, LEFT_RADIUS);
        this.rigthCircle = new Circle(RIGHT_X, CIRCLE_Y, RIGHT_RADIUS);
        this.arrow = new Line(ARROW_X_START, ARROW_Y, ARROW_X_START + ARROW_LENGTH, ARROW_Y);
        this.playerInfo = new PlayerInfo("Default");
        setColors();
    }

    public void initialize() {
        winner = null;
        targetList = new ArrayList<>();
        arrowList = new ArrayList<>();
        playerList = new ArrayList<>();
        targetList.add(new Circle(LEFT_X, CIRCLE_Y, LEFT_RADIUS));
        targetList.add(new Circle(RIGHT_X, CIRCLE_Y, RIGHT_RADIUS));
        updateArrowsPosition();
    }

    private synchronized void updateArrowsPosition() {
        arrowList.clear();
        int clientsCounter = playerList.size();
        for (int i = 1; i <= clientsCounter; i++) {
            double step = Y_BOUND / (clientsCounter + 1);
            arrowList.add(new Line(ARROW_X_START, step * i, ARROW_X_START + ARROW_LENGTH, step * i));
        }
    }

    private void setColors() {
        leftCircle.setFill(Color.RED);
        leftCircle.setStroke(Color.BLACK);
        rigthCircle.setFill(Color.RED);
        rigthCircle.setStroke(Color.BLACK);
        arrow.setStroke(Color.BLACK);
        arrow.setStrokeWidth(ARROW_WIDTH);
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
    }

    public void requestShoot(String playerName) {
    }

    public void requestStop(String playerName) {
    }
}
