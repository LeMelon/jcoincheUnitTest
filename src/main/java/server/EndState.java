package server;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

public class EndState implements State
{
    private int team_one;
    private int team_two;

    public EndState(int result_one, int result_two)
    {
        team_one = result_one;
        team_two = result_two;
    }

    public State exec(ChannelGroup players, Packet s)
    {
        for (Channel p : players)
            p.writeAndFlush(new Packet(null, "Final score :\nTeam1 " + team_one + " Team2 " + team_two, 0));
        return new StartState();
    }
}
