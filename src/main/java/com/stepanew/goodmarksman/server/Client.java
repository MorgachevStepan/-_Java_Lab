package com.stepanew.goodmarksman.server;

import com.google.gson.Gson;
import com.stepanew.goodmarksman.models.Model;
import com.stepanew.goodmarksman.models.ModelBuilder;
import com.stepanew.goodmarksman.models.PlayerInfo;
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
        serverResponse.setEntityList(model.getEntityList());

        socketMessageWrapper.sendMessage(gson.toJson(serverResponse));
    }

    @Override
    public void run() {
        model.addPlayer(playerInfo);
        server.broadcast();

        while (true) {
            String data = socketMessageWrapper.getMessage();

            ClientRequest message = gson.fromJson(data, ClientRequest.class);

            switch (message.getClientActions()) {
                case READY -> model.ready(server, getPlayerName());
                case SHOOT -> model.requestShoot(getPlayerName());
                case STOP -> model.requestStop(getPlayerName());
                case SCORE_TABLE -> model.updateScoreTable(server);
            }
        }
    }
}
