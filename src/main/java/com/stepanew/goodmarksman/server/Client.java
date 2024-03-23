package com.stepanew.goodmarksman.server;

import com.google.gson.Gson;
import com.stepanew.goodmarksman.models.Model;
import com.stepanew.goodmarksman.models.ModelBuilder;
import com.stepanew.goodmarksman.models.PlayerInfo;
import com.stepanew.goodmarksman.server.response.ClientActions;
import com.stepanew.goodmarksman.server.response.ClientRequest;
import com.stepanew.goodmarksman.server.response.ServerResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Client implements Runnable {

    final PlayerInfo playerInfo;
    final Model model;
    final Server server;
    final SocketMessageWrapper socketMessageWrapper;
    final Gson gson;

    public Client(SocketMessageWrapper socketMessageWrapper, Server server, String playerName) {
        this.socketMessageWrapper = socketMessageWrapper;
        this.server = server;
        playerInfo = new PlayerInfo(playerName);
        model = ModelBuilder.build();
        gson = new Gson();
    }

    public String getPlayerName() {
        return playerInfo.getPlayerName();
    }

    public void sendInfoToClient() {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setPlayerInfoList(model.getPlayerList());
        serverResponse.setCircleList(model.getTargetList());
        serverResponse.setLineList(model.getArrowList());
        serverResponse.setWinner(model.getWinner());

        socketMessageWrapper.writeData(gson.toJson(serverResponse));
    }

    @Override
    public void run() {
        System.out.println("Client thread " + playerInfo.getPlayerName());

        model.addPlayer(playerInfo);
        server.broadcast();

        while (true) {
            String data = socketMessageWrapper.getData();
            System.out.println("Message: " + data);

            ClientRequest message = gson.fromJson(data, ClientRequest.class);

            if(message.getClientActions() == ClientActions.READY) {
                System.out.println("READY " + getPlayerName());
                model.ready(server, getPlayerName());
            }

            if(message.getClientActions() == ClientActions.SHOOT) {
                model.requestShoot(getPlayerName());
            }

            if(message.getClientActions() == ClientActions.STOP) {
                model.requestStop(getPlayerName());
            }
        }

    }
}
