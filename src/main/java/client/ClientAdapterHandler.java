package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import server.Card;
import server.CardsColors;
import server.Packet;

import java.util.ArrayList;
import java.util.List;

public class ClientAdapterHandler extends SimpleChannelInboundHandler<Packet>{

    private boolean join = false;
    private boolean get_cards = false;
    public static  boolean get_turn = false;
    private int counter = 0;
    public  static List<Card> hand = new ArrayList<Card>();
    public  static List<Card> played = new ArrayList<Card>();
    public static int lastCall = 0;
    public static CardsColors lastColor;
    public static boolean get_call = false;
    public static boolean get_play = false;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet obj) {
        if (obj.getS() != null && obj.getS().startsWith("["))
            System.out.println(obj.getS());
        else
        {
            if (obj.getCard() != null && !get_call)
            {
                if (get_cards && counter < 8)
                {
                    System.out.println("Card : " + obj.getCard().get_color() + " " + obj.getCard().get_points());
                    hand.add(obj.getCard());
                    counter += 1;
                    if (counter == 8)
                    {
                        get_cards = false;
                        get_call = true;
                    }
                }
                else if (get_play)
                {
                    System.out.println(obj.getS());
                    played.add(obj.getCard());
                    if (played.size() == 4)
                        played.clear();
                }
            }
            else
            {
                if (obj.getS().equals("You have joined"))
                {
                    System.out.println("You can type \"help\" at any time in the game if you feel lost!\n");
                    join = true;
                }
                else if (obj.getS().equals("The Game is starting soon..."))
                {
                    get_cards = true;
                    get_play = false;
                    get_call = false;
                    counter = 0;
                }
                else if (obj.getS().equals("It's your turn !"))
                    get_turn = true;
                else if (obj.getS().equals("It's not your turn !"))
                    get_turn = false;
                else if (obj.getS().startsWith("Last call")) {
                    lastCall = obj.getCall();
                    if (obj.getCard() != null)
                        lastColor = obj.getCard().get_color();
                }
                else if (obj.getS().equals("The game has started !") && get_call)
                {
                    get_call = false;
                    get_play = true;
                }
                else if (obj.getS().equals("New Cards time !") && get_call)
                {
                    get_call = false;
                    counter = 0;
                    hand.clear();
                    get_cards = true;
                }
                System.out.println(obj.getS());
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}