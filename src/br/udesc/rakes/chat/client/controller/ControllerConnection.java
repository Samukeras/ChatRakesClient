package br.udesc.rakes.chat.client.controller;

import br.udesc.rakes.chat.client.view.FrameConnect;
import br.udesc.rakes.chat.common.model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerConnection extends br.udesc.rakes.chat.client.controller.Controller {

    private final FrameConnect frameConnect;


    public ControllerConnection() {
        this.frameConnect = new FrameConnect();

        initComponents();
    }

    @Override
    public void execute() {
        showScreen();
    }

    @Override
    protected void initComponents() {
        this.frameConnect.getBtConnect().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openChatDialog();
            }
        });
    }

    @Override
    protected void showScreen() {
        this.frameConnect.setVisible(true);
    }

    @Override
    protected void hideScreen() {
        this.frameConnect.setVisible(false);
    }

    private void openChatDialog() {
        hideScreen();
        (new ControllerChat(frameConnect,
                            new User(frameConnect.getTfNickname().getText()),
                            frameConnect.getTfAddress().getText(),
                            Integer.parseInt(frameConnect.getTfPort().getText()))).execute();
    }

}