package br.udesc.rakes.chat.client.model;

import br.udesc.rakes.chat.common.model.ChatMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteService {

    private Socket             socket;
    private ObjectOutputStream output;

    private String address;
    private int    port;


    public ClienteService(String address, int port) {
        this.address = address;
        this.port    = port;
    }


    public Socket connect() {
        try {
            this.socket = new Socket(address, port);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return socket;
    }

    public void send(ChatMessage message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}