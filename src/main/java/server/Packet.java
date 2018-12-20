package server;

import java.io.Serializable;

public class Packet implements Serializable {
    private Card card = new Card(null, null);
    private String s;
    private int call;

    public Packet(Card _card, String _s, int _call) {
        card = _card;
        s = _s;
        call = _call;
    }

    public Card getCard() {
        return card;
    }

    public String getS() {
        return s;
    }

    public int getCall() {
        return call;
    }
}
