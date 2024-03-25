package com.stepanew.goodmarksman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameBoardView {

    @FXML
    VBox infoBox;

    @FXML
    Pane gameBoard;

    @FXML
    VBox playersBox;

    public void displayCircle(Circle circle) {
        gameBoard.getChildren().add(circle);
    }

    public void removeCircle() {
        gameBoard.getChildren().remove(gameBoard.getChildren().size() - 1);
    }

    public void displayArrow(Line arrow) {
        gameBoard.getChildren().add(arrow);
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
