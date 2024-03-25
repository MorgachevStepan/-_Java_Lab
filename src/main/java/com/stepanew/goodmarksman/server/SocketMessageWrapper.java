package com.stepanew.goodmarksman.server;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.*;
import java.net.Socket;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocketMessageWrapper {

    final BufferedReader input;
    final PrintWriter output;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;

    public SocketMessageWrapper(Socket socket) {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessage() {
        try {
            return input.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getData() {
        try {
            return dataInputStream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeData(String message) {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

}
