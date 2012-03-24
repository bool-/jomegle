package net.ifixshit.omegle;

import java.io.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Omegle extends Thread {
    public static final String OMEGLE_SERVERS[] = {"promenade.omegle.com", "bajor.omegle.com",
            "quarks.omegle.com", "odo-bucket.omegle.com", "chatserv.omegle.com", "cardassia.omegle.com"};
    private DataOutputStream output;
    private DataInputStream input;
    private List<OmegleListener> listeners = new ArrayList<OmegleListener>();
    private boolean connected;

    public Omegle() {
    }

    public void addListener(OmegleListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OmegleListener listener) {
        listeners.remove(listener);
    }

    public void run() {
        try {
            Socket socket = new Socket(OMEGLE_SERVERS[new Random().nextInt(OMEGLE_SERVERS.length)], 1365);
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
            this.connected = true;
            writePacket("omegleStart", null);
            while (socket.isConnected() && connected) {
                try {
                    byte opcode_length = (byte) input.read();
                    String opcode = readString(opcode_length);
                    short data_length = input.readShort();
                    String data = readString(data_length);
                    if (opcode.equals("c")) {
                        for (OmegleListener listener : listeners) {
                            listener.connected();
                        }
                    } else if (opcode.equals("count")) {
                        for (OmegleListener listener : listeners) {
                            listener.count(Integer.parseInt(data));
                        }
                    } else if (opcode.equals("m")) {
                        for (OmegleListener listener : listeners) {
                            listener.messageReceived(data);
                        }
                    } else if (opcode.equals("t")) {
                        for (OmegleListener listener : listeners) {
                            listener.partnerTyping();
                        }
                    } else if (opcode.equals("w")) {
                        for (OmegleListener listener : listeners) {
                            listener.waiting();
                        }
                    } else if (opcode.equals("st")) {
                        for (OmegleListener listener : listeners) {
                            listener.partnerStopTyping();
                        }
                    } else if (opcode.equals("d")) {
                        for (OmegleListener listener : listeners) {
                            connected = false;
                            listener.disconnected();
                        }
                    } else if (opcode.equals("recaptchaRequired")) {
                        for (OmegleListener listener : listeners) {
                            listener.recaptchaRequired(data.split("=")[1]);
                        }
                    } else if (opcode.equals("recaptchaRejected")) {
                        for (OmegleListener listener : listeners) {
                            listener.recaptchaRejected(data.split("=")[2], data.split("=")[1].replace("&publicKey", ""));
                        }
                    } else {
                        for (OmegleListener listener : listeners) {
                            listener.unknownEvent(opcode, data);
                        }
                    }
                } catch (EOFException ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        writePacket("d", null);
        connected = false;
    }

    public void typing() {
        writePacket("t", null);
    }

    public void sendMessage(String msg) {
        writePacket("s", msg);
    }

    public void writePacket(String opcode, String data) {
        try {
            output.writeByte(opcode.length());
            output.writeBytes(opcode);
            if (data != null) {
                output.writeShort(data.length());
                output.writeBytes(data);
            } else {
                output.writeShort(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readString(short length) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < length) {
            builder.append((char) ((byte) input.read()));
        }
        return builder.toString();
    }
}