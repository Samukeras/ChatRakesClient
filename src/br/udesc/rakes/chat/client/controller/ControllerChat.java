package br.udesc.rakes.chat.client.controller;

import br.udesc.rakes.chat.client.model.ClienteService;
import br.udesc.rakes.chat.client.view.DialogChat;
import br.udesc.rakes.chat.common.model.Action;
import br.udesc.rakes.chat.common.model.ChatMessage;
import br.udesc.rakes.chat.common.model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

public class ControllerChat extends Controller {

    private DialogChat dialogChat;

    private Socket         socket;
    private ChatMessage    message;
    private ClienteService service;

    private User   user;
    private String address;
    private int    port;


    public ControllerChat(java.awt.Frame parent, User user, String address, int port) {
        this.dialogChat = new DialogChat(parent, true);
        dialogChat.setTitle(address + ":" + port + " - " + user.getNickname());

        this.user    = user;
        this.address = address;
        this.port    = port;

        initComponents();
        startService();
    }



    private class ListenerSocket implements Runnable {

        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            ChatMessage message = null;
            try {
                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) {
                        connected(message);
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnected();
                        socket.close();
                    } else if (action.equals(Action.SEND_ONE)) {
                        System.out.println("::: " + message.getMessage() + " :::");
                        receive(message);
                    } else if (action.equals(Action.USERS_ONLINE)) {
                        refreshOnlines(message);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void startService() {
        this.service = new ClienteService(this.address, this.port);
        this.socket = this.service.connect();

        new Thread(new ControllerChat.ListenerSocket(this.socket)).start();

        this.message = new ChatMessage();
        this.message.setAction(Action.CONNECT);
        this.message.setSender(user);
        this.service.send(message);
    }

    private void connected(ChatMessage message) {
        this.message = message;
        this.dialogChat.getBtSend().setEnabled(false);

        this.dialogChat.getBtClose().setEnabled(true);
        this.dialogChat.getTaMessage().setEnabled(true);
        this.dialogChat.getTaReceive().setEnabled(true);

        this.dialogChat.getBtSend().setEnabled(true);

        JOptionPane.showMessageDialog(dialogChat, "Você está conectado no chat!");
    }

    private void disconnected() {
        this.dialogChat.getBtClose().setEnabled(false);
        this.dialogChat.getTaMessage().setEnabled(false);
        this.dialogChat.getTaReceive().setEnabled(false);
        this.dialogChat.getBtSend().setEnabled(false);

        this.dialogChat.getTaReceive().setText("");
        this.dialogChat.getTaMessage().setText("");

        JOptionPane.showMessageDialog(this.dialogChat, "Você saiu do chat!");
    }

    private void receive(ChatMessage message) {
        this.dialogChat.getTaReceive().append(message.getSender().getNickname() + " diz: " + message.getMessage() + "\n");
    }

    private void refreshOnlines(ChatMessage message) {
        System.out.println(message.getSetOnlines().toString());

        Set<User> names = message.getSetOnlines();

        names.remove(message.getSender());

        User[] array = (User[]) names.toArray(new User[names.size()]);

        this.dialogChat.getListUsers().setListData(array);
        this.dialogChat.getListUsers().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.dialogChat.getListUsers().setLayoutOrientation(JList.VERTICAL);
    }

    @Override
    protected void initComponents() {
        dialogChat.getBtClose().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickClose();
            }
        });

        dialogChat.getBtSend().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClickSend();
            }
        });
    }

    private void onClickClose() {
        ChatMessage message = new ChatMessage();
        message.setSender(user);
        message.setAction(Action.DISCONNECT);
        this.service.send(message);
        disconnected();
    }

    private void onClickSend() {
        String text = this.dialogChat.getTaMessage().getText();
        String name = user.getNickname();

        this.message = new ChatMessage();


        if (this.dialogChat.getListUsers().getSelectedIndex() > -1) {
            this.message.setReceiver((User) this.dialogChat.getListUsers().getSelectedValue());
            this.message.setAction(Action.SEND_ONE);
            this.dialogChat.getListUsers().clearSelection();
        } else {
            this.message.setAction(Action.SEND_ALL);
        }

        if (!text.isEmpty()) {
            this.message.setSender(user);
            this.message.setMessage(text);

            this.dialogChat.getTaReceive().append("Você disse: " + text + "\n");

            this.service.send(this.message);
        }

        this.dialogChat.getTaMessage().setText("");
    }

    @Override
    protected void showScreen() {
        this.dialogChat.setVisible(true);
    }

    @Override
    protected void hideScreen() {
        this.dialogChat.setVisible(false);
    }

}