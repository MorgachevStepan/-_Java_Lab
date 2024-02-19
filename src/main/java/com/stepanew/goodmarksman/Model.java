package com.stepanew.goodmarksman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Model {

    Line arrow;
    Circle leftCircle;
    Circle rigthCircle;
    int shotCounter;
    int scoreCounter;

    final double ARROW_LENGTH = 60;
    final double ARROW_WIDTH = 5;
    final double ARROW_Y = 154;
    final double ARROW_X_START = -27;
    final double LEFT_X = 50;
    final double CIRCLE_Y = 120;
    final double LEFT_RADIUS = 18;
    final double RIGHT_RADIUS = 9;
    final double RIGHT_X = 100;


    public Model(){
        this.leftCircle = new Circle(LEFT_X, CIRCLE_Y, LEFT_RADIUS);
        this.rigthCircle = new Circle(RIGHT_X, CIRCLE_Y, RIGHT_RADIUS);
        this.arrow = new Line(ARROW_X_START, ARROW_Y, ARROW_X_START + ARROW_LENGTH, ARROW_Y);
        this.shotCounter = 0;
        setColors();
    }

    private void setColors() {
        leftCircle.setFill(Color.RED);
        leftCircle.setStroke(Color.BLACK);
        rigthCircle.setFill(Color.RED);
        rigthCircle.setStroke(Color.BLACK);
        arrow.setStroke(Color.BLACK);
        arrow.setStrokeWidth(ARROW_WIDTH);
    }

    public void setLeftCenterY(double yCoordinate){
        leftCircle.setCenterY(yCoordinate);
    }

    public void setRightCenterY(double yCoordinate){
        rigthCircle.setCenterY(yCoordinate);
    }

    public double getLeftCenterY(){
        return leftCircle.getCenterY();
    }

    public double getRightCenterY(){
        return rigthCircle.getCenterY();
    }

    public double getLeftCenterX(){
        return leftCircle.getCenterX();
    }

    public double getRightCenterX(){
        return rigthCircle.getCenterX();
    }

    public double getArrowEndX(){
        return arrow.getEndX();
    }

    public double getArrowEndY(){
        return arrow.getEndY();
    }

    public void moveArrow(double deltaX){
        arrow.setStartX(arrow.getStartX() + deltaX);
        arrow.setEndX(arrow.getEndX() + deltaX);
    }

    public void resetArrowCoordinates() {
        arrow.setStartX(ARROW_X_START);
        arrow.setEndX(ARROW_X_START + ARROW_LENGTH);
    }

    public int incrementShotCounter() {
        return ++shotCounter;
    }

    public int incrementScoreCounter(int score) {
        scoreCounter += score;
        return scoreCounter;
    }

}
