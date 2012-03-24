package net.ifixshit.omegle.impl;

import net.ifixshit.omegle.Omegle;
import net.ifixshit.omegle.OmegleListener;

import java.util.Scanner;

public class TerminalOmegle extends Thread implements OmegleListener {


    private Omegle omegle;

    public void initiate() {
        omegle = new Omegle();
        omegle.addListener(this);
        omegle.start();
    }
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            omegle.sendMessage(line);
        }
    }

    public void connected() {
        System.out.println("Partner connected");
    }

    public void disconnected() {
        System.out.println("Partner disconnected");
        initiate();
    }

    public void messageReceived(String message) {
        System.out.println("Stranger: " + message);
    }

    public void unknownEvent(String event, String data) {
        System.out.println("[UNKNOWN EVENT] " + event + ":" + data);
    }

    public void partnerTyping() {
    }

    public void partnerStopTyping() {
    }

    public void waiting() {
    }

    public void count(int count) {
    }

    public void recaptchaRequired(String key) {
    }

    public void recaptchaRejected(String key, String error) {
    }


    public static void main(String[] args) {
        TerminalOmegle omegle = new TerminalOmegle();
        omegle.initiate();
        omegle.start();
    }
}
