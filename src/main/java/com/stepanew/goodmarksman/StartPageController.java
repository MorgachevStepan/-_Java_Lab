package com.stepanew.goodmarksman;

import com.google.gson.Gson;
import com.stepanew.goodmarksman.models.Model;
import com.stepanew.goodmarksman.models.ModelBuilder;
import com.stepanew.goodmarksman.server.SocketMessageWrapper;
import com.stepanew.goodmarksman.server.response.ServerResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartPageController {

    Socket socket;
    int PORT = 8080;
    InetAddress ip;
    SocketMessageWrapper socketMessageWrapper;
    Model model;

    @FXML
    TextField nameField;

    {
        ip = null;
        model = ModelBuilder.build();
    }

    @FXML
    void onConnect() {
        try {
            ip = InetAddress.getLocalHost();
            socket = new Socket(ip, PORT);
            socketMessageWrapper = new SocketMessageWrapper(socket);
            socketMessageWrapper.sendMessage(nameField.getText().trim());
            String response = socketMessageWrapper.getMessage();

            if(response.equals("ACCEPT")) {
                new Thread(
                        () -> {
                            while (true) {
                                String data = socketMessageWrapper.getData();
                                System.out.println("Response: " + data);
                                Gson gson = new Gson();
                                ServerResponse answer = gson.fromJson(data, ServerResponse.class);
                                System.out.println("Answer " + answer);
                                model.setPlayerList(answer.getPlayerInfoList());
                                model.setTargetList(answer.getCircleList());
                                model.setArrowList(answer.getLineList());
                                model.setWinner(answer.getWinner());
                                model.update();
                            }
                        }
                ).start();
                openGamePage();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка");
                alert.setContentText(response);

                alert.showAndWait();
                nameField.setText("");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void openGamePage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gameboard-view.fxml"));
            Parent root1 = fxmlLoader.load();
            Scene scene = new Scene(root1, 1100, 660);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setResizable(true);
            stage.setTitle("Java Shooter Game.");
            stage.setScene(scene);
            stage.show();

            GameBoardController clientFrame = fxmlLoader.getController();
            clientFrame.dataInit(socketMessageWrapper, nameField.getText().trim());
            model.update();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
