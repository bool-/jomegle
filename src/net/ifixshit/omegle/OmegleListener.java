package net.ifixshit.omegle;

public interface OmegleListener {
    public void recaptchaRequired(String key);

    public void recaptchaRejected(String key, String error);

    public void connected();

    public void disconnected();

    public void messageReceived(String message);

    public void unknownEvent(String event, String data);

    public void partnerTyping();

    public void partnerStopTyping();

    public void waiting();

    public void count(int count);
}

