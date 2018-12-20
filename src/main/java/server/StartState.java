package server;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartState implements State
{
    public StartState()
    {
    }

    public State exec(ChannelGroup players, Packet s)
    {
        if (players.size() == 4)
        {
            List<Card> list = new ArrayList<Card>();
            Random random = new Random();
            int rand;
            List<Player>    info_players = new ArrayList<Player>();

            for (CardsColors color : CardsColors.values())
                for (CardsPoints point : CardsPoints.values())
                    list.add(new Card(point, color));
            int counter = 0;
            for (Channel p : players)
            {
                p.writeAndFlush(new Packet(null, "The Game is starting soon...", 0));

                List<Card>      hand_cards = new ArrayList<Card>();
                Player          player;

                if (list.size() != 8)
                {
                    for (int count = 0; count < 8; count++)
                    {
                        rand = random.nextInt(list.size() - 1);
                        hand_cards.add(list.get(rand));
                        list.remove(rand);
                    }
                    for (Card c : hand_cards)
                        p.writeAndFlush(new Packet(c, null, 0));

                    player = new Player(p, hand_cards);
                }
                else
                {
                    for (Card c : list)
                        p.writeAndFlush(new Packet(c, null, 0));
                    player = new Player(p, list);
                }
                if (counter % 2 == 1)
                    p.writeAndFlush(new Packet(null, "You are in the Team2 : Player2 / Player4 ", 0));
                else
                    p.writeAndFlush(new Packet(null, "You are in the Team1 : Player1 / Player3", 0));
                info_players.add(player);
                counter += 1;
            }
            return new CallState(info_players);
        }
        else
            return this;
    }
}
