package com.stepanew.goodmarksman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Model {

    Circle leftCircle;

    Circle rigthCircle;

    final double LEFT_X = 50;

    final double CIRCLE_Y = 120;

    final double LEFT_RADIUS = 18;
    final double RIGHT_RADIUS = 9;

    final double RIGHT_X = 100;


    public Model(){
        this.leftCircle = new Circle(LEFT_X, CIRCLE_Y, LEFT_RADIUS);
        this.rigthCircle = new Circle(RIGHT_X, CIRCLE_Y, RIGHT_RADIUS);
        setColors();
    }

    private void setColors() {
        leftCircle.setFill(Color.RED);
        leftCircle.setStroke(Color.BLACK);
        rigthCircle.setFill(Color.RED);
        rigthCircle.setStroke(Color.BLACK);
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

}
