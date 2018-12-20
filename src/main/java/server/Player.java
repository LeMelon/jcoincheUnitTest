package server;

import io.netty.channel.Channel;

import java.util.List;

public class Player
{
    private Channel channel;
    private List<Card> pack;

    public Player(Channel add_chan, List<Card> add_pack)
    {
        channel = add_chan;
        pack = add_pack;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<Card> getPack() {
        return pack;
    }

    public void setPack(List<Card> pack) {
        this.pack = pack;
    }
}
