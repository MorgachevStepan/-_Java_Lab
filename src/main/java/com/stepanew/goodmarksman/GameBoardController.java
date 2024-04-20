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
import com.stepanew.goodmarksman.store.PlayerEntity;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import javafx.scene.shape.Circle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class GameBoardController extends GameBoardView implements IObserver {

    final Model model;
    String playerName;
    SocketMessageWrapper socketMessageWrapper;
    boolean IS_SHOW_TABLE;
    final Gson gson;
    final List<Button> players;
    final List<VBox> playersInfo;
    final List<Circle> targets;
    final List<Line> arrows;
    final double BUTTON_SIZE = 140;

    {
        players = new ArrayList<>();
        playersInfo = new ArrayList<>();
        targets = new ArrayList<>();
        arrows = new ArrayList<>();
        model = ModelBuilder.build();
        gson = new Gson();
        IS_SHOW_TABLE = false;
    }


    public void initialize(){
        model.addObserver(this);
    }

    @FXML
    void ready() {
        sendRequest(new ClientRequest(ClientActions.READY));
    }

    @FXML
    void shoot() {
        sendRequest(new ClientRequest(ClientActions.SHOOT));
    }

    @FXML
    void pause() {
        sendRequest(new ClientRequest(ClientActions.STOP));
    }

    @FXML
    void tableScore() {
        sendRequest(new ClientRequest(ClientActions.SCORE_TABLE));
        IS_SHOW_TABLE = true;
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

        if (IS_SHOW_TABLE && model.getEntityList() != null && model.getEntityList().size() != 0) {
            showScoreTable();
            IS_SHOW_TABLE = false;
        }
    }

    private void showScoreTable() {
        Platform.runLater(() -> {
            TableView<PlayerEntity> tableView = new TableView<>();

            TableColumn<PlayerEntity, String> column1 =
                    new TableColumn<>("Имя");

            column1.setCellValueFactory(
                    new PropertyValueFactory<>("name"));

            TableColumn<PlayerEntity, String> column2 =
                    new TableColumn<>("Победы");

            column2.setCellValueFactory(
                    new PropertyValueFactory<>("wins"));

            tableView.getColumns().add(column1);
            tableView.getColumns().add(column2);

            model.getEntityList().forEach(tableView.getItems()::add);

            VBox vbox = new VBox(tableView);
            Scene scene = new Scene(vbox);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Таблица лидеров");
            stage.show();
        });
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
                    button.setPrefHeight(BUTTON_SIZE);
                    button.setPrefWidth(BUTTON_SIZE);

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
            for (int i = 0; i < playerList.size(); i++) {
                if (i >= players.size()) {
                    VBox vBox = PlayerInfoController.createPlayerInfoVBox(playerList.get(i));
                    playersInfo.add(vBox);
                    addPlayerInfo(vBox);
                } else {
                    PlayerInfoController.setPlayerName(playersInfo.get(i), playerList.get(i).getPlayerName());
                    PlayerInfoController.setShotCounter(playersInfo.get(i), playerList.get(i).getShotCounter());
                    PlayerInfoController.setScoreCounter(playersInfo.get(i), playerList.get(i).getScoreCounter());
                    PlayerInfoController.setWinCounter(playersInfo.get(i), playerList.get(i).getWins());
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
                alert.setContentText(
                        "Победитель : " +
                                ((model.getWinner()).equals(this.playerName)
                                        ? "Вы"
                                        : model.getWinner()) + "!"
                );
                alert.showAndWait();  
            });
        }
    }
}
