package com.stepanew.goodmarksman;

import com.stepanew.goodmarksman.models.PlayerInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameBoardView {

    final static double CIRCLES_PANE_HIGH_HEIGHT = 330.0;
    final static double CIRCLES_PANE_LOW_HEIGHT = 20.0;
    final static double ARROWS_PANE_END = 400.0;
    final static String DEFAULT_VALUE = "0";
    final ArrayList<VBox> playersInfo = new ArrayList<>();

    @FXML
    VBox infoBox;

    @FXML
    Pane gameBoard;

    @FXML
    VBox playersBox;

    public void displayCircle(Circle circle) {
        gameBoard.getChildren().add(circle);
    }

    public void displayArrow(Line arrow) {
        gameBoard.getChildren().add(arrow);
    }

    protected void clearCirclesPane(Circle circle) {
        gameBoard.getChildren().remove(circle);
    }

    public void clearArrowsPane(Line arrow) {
        gameBoard.getChildren().remove(arrow);
    }

    public void addPlayerInfo(VBox vBox){
        infoBox.getChildren().add(vBox);
    }

    public void addPlayersBox(Button button) {
        playersBox.getChildren().add(button);
    }

}
