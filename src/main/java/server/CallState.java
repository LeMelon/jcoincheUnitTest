package server;

import io.netty.channel.group.ChannelGroup;

import java.util.List;

public class CallState implements State
{
    private List<Player> info_players;
    private int turn = 0;
    private int last_call = 0;
    private CardsColors color;
    private int pass = 0;
    private boolean team = false;
    private static boolean start = false;
    public CallState(List<Player> info_p)
    {
        info_players = info_p;
    }

    public State exec(ChannelGroup players, Packet s)
    {
        int counter = 0;
        if (turn == 4)
            turn = 0;

        if (s.getCall() > 0 && s.getCard().get_color() != null)
        {
            last_call = s.getCall();
            if (turn % 2 == 1)
                team = false;
            else
                team = true;
            color = s.getCard().get_color();
            pass = 0;
        }
        else if (s.getCall() < 0)
            pass += 1;

        if (pass == 3)
        {
            if (last_call == 0)
            {
                for (Player player : info_players)
                    player.getChannel().writeAndFlush(new Packet(null, "New Cards time !", 0));
                return new StartState();
            }
            for (Player player : info_players)
            {
                player.getChannel().writeAndFlush(new Packet(null, "The game has started !", 0));
                if (turn % 2 == 0)
                    player.getChannel().writeAndFlush(new Packet(null, "Strong Color : " + color + " Call : " + last_call +
                            " for Team1", 0));
                else
                    player.getChannel().writeAndFlush(new Packet(null, "Strong Color : " + color + " Call : " + last_call +
                            " for Team2", 0));
            }
            return new PlayState(info_players, last_call, color, team);
        }

        for (Player player : info_players)
        {
            Card card = new Card(null, color);

            if (start)
            player.getChannel().writeAndFlush(new Packet(card, "Last call : " + last_call + " " + color, last_call));

            if (counter == turn)
                player.getChannel().writeAndFlush(new Packet(null, "It's your turn !", 0));
            else
                player.getChannel().writeAndFlush(new Packet(null, "It's not your turn !", 0));
            counter += 1;
        }
        start = true;
        turn += 1;
        return this;
    }
}
