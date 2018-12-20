package server;

import io.netty.channel.group.ChannelGroup;

public interface State
{
    public State exec(ChannelGroup players, Packet s);
}
