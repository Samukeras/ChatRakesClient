package br.udesc.rakes.chat.client.controller;

public abstract class Controller {

    public void execute() {
        showScreen();
    }

    protected abstract void initComponents();

    protected abstract void showScreen();

    protected abstract void hideScreen();

}