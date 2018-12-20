package server;

import io.netty.channel.group.ChannelGroup;

public class Core
{
    static State state = new StartState();

    public Core()
    {

    }

    public boolean loop(ChannelGroup players, Packet s)
    {
        State new_state;

        new_state = state.exec(players, s);
        while (new_state.getClass() != state.getClass())
        {
            state = new_state;
            new_state = state.exec(players, s);
        }
        state = new_state;

        return false;
    }
    public void initState()
    {
        state = new StartState();
    }
}
