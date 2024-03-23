package com.stepanew.goodmarksman.server;

import com.stepanew.goodmarksman.models.Model;
import com.stepanew.goodmarksman.models.ModelBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Server {

    final int PORT = 8080;
    InetAddress ip = null;
    final ExecutorService service = Executors.newFixedThreadPool(4);
    final Model model = ModelBuilder.build();
    final List<Client> clientList;

    {
        clientList = new ArrayList<>();
    }

    public void broadcast() {
        clientList.forEach(Client::sendInfoToClient);
    }

    public void startServer() {
        try {
            ip = InetAddress.getLocalHost();
            ServerSocket serverSocket = new ServerSocket(PORT, 2, ip);
            System.out.append("Server start\n");
            model.initialize();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                SocketMessageWrapper socketMessageWrapper = new SocketMessageWrapper(clientSocket);
                String responseName = socketMessageWrapper.getMessage();

                if(tryAddClient(socketMessageWrapper, responseName)) {
                    System.out.println(responseName + " Connected");
                } else {
                    clientSocket.close();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean tryAddClient(SocketMessageWrapper socketMessageWrapper, String responseName) {
        if (clientList.size() >= 4) {
            socketMessageWrapper.sendMessage("Превышено максимальное число подключений");
            return false;
        }

        if (checkClientList(responseName)) {
            socketMessageWrapper.sendMessage("ACCEPT");
            Client client = new Client(socketMessageWrapper, this, responseName);
            clientList.add(client);
            service.submit(client);
            System.out.println("RESPONSE ACCEPT");
            return true;
        }

        socketMessageWrapper.sendMessage("Уже имеется игрок с таким именем");
        System.out.println("RESPONSE DECLINE");
        return false;
    }

    private boolean checkClientList(String responseName) {
        return clientList.isEmpty() ||
                clientList.stream()
                        .filter(client -> client.getPlayerName().equals(responseName))
                        .findFirst()
                        .orElse(null) == null;
    }

    public static void main(String[] args) {
        new Server().startServer();
    }

}
