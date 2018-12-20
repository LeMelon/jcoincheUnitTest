package server;

import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.List;

public class PlayState implements State
{
    private List<Player> info_players;
    private int call;
    private CardsColors color;
    private int turn = 0;
    private List<Card> round = new ArrayList<Card>();
    private static int team_one = 0;
    private static int team_two = 0;
    private int score_one = 0;
    private int score_two = 0;
    private boolean start = false;
    private boolean team_round;

    public PlayState(List<Player> info, int last_call, CardsColors last_color, boolean team)
    {
        info_players = info;
        call = last_call;
        color = last_color;
        team_round = team;
    }

    private void give_points()
    {
        if (score_one % 10 >= 5)
            score_one += (10 - (score_one % 10));
        else
            score_one -= (score_one % 10);
        if (score_two % 10 >= 5)
            score_two += (10 - (score_two % 10));
        else
            score_two -= (score_two % 10);

        team_one += score_one;
        team_two += score_two;
        for (Player player : info_players)
            player.getChannel().writeAndFlush(new Packet(null, "Team1 get " + score_one + " and Team2 get " + score_two + "\n" +
                    "Result : Team1 " + team_one + " / Team 2 " + team_two, 0));
    }

    public State exec(ChannelGroup players, Packet s)
    {
        int counter = 0;


        if (turn == 4)
            turn = 0;
        if (start)
            round.add(s.getCard());


        if (round.size() == 4)
        {

            int value = 0;
            int result = 0;
            CardsColors round_color = round.get(0).get_color();
            int count = 0;
            for (Card played : round)
            {
                System.out.println(played.get_points() + " " + played.get_color() + " ");
                if (count == 0) {
                    if (played.get_color() == color && played.get_points() == CardsPoints.VALET)
                        played.get_points().setCards(20);
                    else if (played.get_color() == color && played.get_points() == CardsPoints.NINE)
                        played.get_points().setCards(14);
                    value = played.get_points().getCards();
                    result += played.get_points().getCards();
                    if (played.get_points() == CardsPoints.VALET)
                        played.get_points().setCards(2);
                    else if (played.get_points() == CardsPoints.NINE)
                        played.get_points().setCards(0);
                }
                else
                {
                    if (played.get_color() == round_color)
                    {
                        if (round_color == color)
                        {
                            if (played.get_points() == CardsPoints.VALET)
                                played.get_points().setCards(20);
                            else if (played.get_points() == CardsPoints.NINE)
                                played.get_points().setCards(14);
                        }
                        if (played.get_points().getCards() > value)
                        {
                            value = played.get_points().getCards();
                        }
                        result += played.get_points().getCards();
                        if (played.get_points() == CardsPoints.VALET)
                            played.get_points().setCards(2);
                        else if (played.get_points() == CardsPoints.NINE)
                            played.get_points().setCards(0);

                    }
                    else if (played.get_color() == color)
                    {
                        if (played.get_points() == CardsPoints.VALET)
                            played.get_points().setCards(20);
                        else if (played.get_points() == CardsPoints.NINE)
                            played.get_points().setCards(14);
                        round_color = color;
                        value = played.get_points().getCards();
                        result += played.get_points().getCards();
                        if (played.get_points() == CardsPoints.VALET)
                            played.get_points().setCards(2);
                        else if (played.get_points() == CardsPoints.NINE)
                            played.get_points().setCards(0);

                    }
                    else
                    {
                        result += played.get_points().getCards();
                        if (played.get_points() == CardsPoints.VALET)
                            played.get_points().setCards(2);
                        else if (played.get_points() == CardsPoints.NINE)
                            played.get_points().setCards(0);

                    }
                }
                count += 1;
            }

            count = 0;
            for (Card played : round)
            {
                if (played.get_color() == color && played.get_points() == CardsPoints.VALET)
                    played.get_points().setCards(20);
                else if (played.get_color() == color && played.get_points() == CardsPoints.NINE)
                    played.get_points().setCards(14);
                if (played.get_points().getCards() == value
                        && played.get_color() == round_color)
                {
                    if (count + turn % 2 == 1) {
                        score_two += result;
                        if (score_two + score_one == 152) {
                            score_two += 10;
                        }
                    }
                    else {
                        score_one += result;
                        if (score_two + score_one == 152) {
                            score_one += 10;
                        }
                    }
                    if (played.get_points() == CardsPoints.VALET)
                        played.get_points().setCards(2);
                    else if (played.get_points() == CardsPoints.NINE)
                        played.get_points().setCards(0);
                    break ;
                }
                if (played.get_points() == CardsPoints.VALET)
                    played.get_points().setCards(2);
                else if (played.get_points() == CardsPoints.NINE)
                    played.get_points().setCards(0);

                count += 1;
            }
            if (turn + count > 3)
                turn = turn + count - 4;
            else
                turn = turn + count;
            round.clear();
        }

        if (score_two + score_one == 162)
        {
            if (team_round)
            {
                if (score_one >= call - 5)
                    give_points();
                else {
                    team_two += 160;
                    for (Player player : info_players)
                        player.getChannel().writeAndFlush(new Packet(null, "Team2 win this round with 160 points !\n" +
                                "Result : Team1 " + team_one + " / Team 2 " + team_two, 0));
                }
            }
            else
            {
                if (score_two >= call - 5)
                    give_points();
                else {
                    team_one += 160;
                    for (Player player : info_players)
                        player.getChannel().writeAndFlush(new Packet(null, "Team1 win this round with 160 points !\n" +
                                "Result : Team1 " + team_one + " / Team 2 " + team_two, 0));
                }

            }
            if (team_one > 1000 || team_two > 1000)
            {
                int result_one = team_one;
                int result_two = team_two;
                if (team_one > 1000)
                    for (Player player : info_players)
                        player.getChannel().writeAndFlush(new Packet(null, "Team1 Win !", 0));
                else
                    for (Player player : info_players)
                        player.getChannel().writeAndFlush(new Packet(null, "Team2 Win !", 0));
                team_one = 0;
                team_two = 0;
                return new EndState(result_one, result_two);
            }
            else
                return new StartState();
        }

        for (Player player : info_players)
        {
            if (start)
                player.getChannel().writeAndFlush(new Packet(s.getCard(),
                        "Player " + (turn + 1) + " : " + s.getCard().get_points() + " " + s.getCard().get_color(),0));
            if (counter == turn)
                player.getChannel().writeAndFlush(new Packet(null, "It's your turn !", 0));
            else
                player.getChannel().writeAndFlush(new Packet(null, "It's not your turn !", 0));
            counter += 1;
        }

        turn += 1;
        start = true;
        return this;
    }
}
