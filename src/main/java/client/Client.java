package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import server.Card;
import server.CardsColors;
import server.Packet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static client.ClientAdapterHandler.*;

public final class Client {

    static String HOST;
    static int PORT;

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        if (args[0] != null && args[1] != null) {

            if (!(args[1].matches("^-?\\d+$"))) {
                System.out.println("The host must be an integer");
                return ;
            }
            HOST = args[0];
            PORT = Integer.parseInt(args[1]);
            final SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ClientAdapterInitializer(sslCtx));

                // Start the connection attempt.
                Channel ch = b.connect(HOST, PORT).sync().channel();

                // Read commands from the stdin.
                ChannelFuture lastWriteFuture = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    } else if ("cards".equals(line.toLowerCase()) && hand.size() != 0) {
                        System.out.println("You have : ");
                        int counter = 0;
                        for (Card i : hand)
                        {
                            counter += 1;
                            System.out.println(counter + " " + i.get_color() + " " + i.get_points());
                        }
                        System.out.print("\n");
                    } else if ("help".equals(line.toLowerCase())) {
                        if (get_play) {
                            System.out.println("You can type \"cards\" to see your hand");
                            System.out.println("You can type\"played\" to see which cards had been played");
                            System.out.println("You have to type \"play numberOfCard\" to play a card example ; \"play 2\"\nThe cards command will help you");
                        }
                        else if (get_call) {
                            System.out.println("You can type \"cards\" to see your hand");
                            System.out.println("You can type \"pass\" if you have a very bad hand !");
                            System.out.println("You have to type \"numberOfPointsToAchieved strongColor\" example : \"90 heart\"");
                        }
                    } else if (get_turn && get_call) {
                        if ("pass".equals(line.toLowerCase()))
                            ch.writeAndFlush(new Packet(null, null, -1));
                        checkCall(line, ch);
                    } else if (get_play && "played".equals(line.toLowerCase())) {
                        for (Card card : played) {
                            if (card.equals(played.get(0)))
                                System.out.println("First Card : type " + card.get_points() + " / color " + card.get_color());
                            System.out.println("Card : type " + card.get_points() + " / color " + card.get_color());
                        }
                    } else if (get_turn && get_play && line.toLowerCase().startsWith("play")) {
                        playCard(line, ch);
                    } else
                        lastWriteFuture = ch.writeAndFlush(new Packet(null, line + "\r\n", 0));
                    if ("quit".equals(line.toLowerCase())) {
                        ch.closeFuture().sync();
                        break;
                    }
                }

                // Wait until all messages are flushed before closing the channel.
                if (lastWriteFuture != null) {
                    lastWriteFuture.sync();
                }
            } finally {
                // The connection is closed automatically on shutdown.
                group.shutdownGracefully();
            }
        }
    }

    public static int checkCall(String s, Channel ch) {
        if (s.indexOf(" ") == -1)
            return 1;
        System.out.println("[" + s.substring(0, s.indexOf(" ")) + "]");
        if (s.substring(0, s.indexOf(" ")).matches("^-?\\d+$")) {
            if (Integer.parseInt(s.substring(0, s.indexOf(" "))) <= 180 && Integer.parseInt(s.substring(0, s.indexOf(" "))) >= 90)
                if (Integer.parseInt(s.substring(0, s.indexOf(" "))) % 10 == 0)
                    if (Integer.parseInt(s.substring(0, s.indexOf(" "))) > lastCall) {
                        if ("tile".equals(s.substring(s.lastIndexOf(" ") + 1, s.length()).toLowerCase()))
                            ch.writeAndFlush(new Packet(new Card(null, CardsColors.TILE), null, Integer.parseInt(s.substring(0, s.indexOf(" ")))));
                        if ("clover".equals(s.substring(s.lastIndexOf(" ") + 1, s.length()).toLowerCase()))
                            ch.writeAndFlush(new Packet(new Card(null, CardsColors.CLOVER), null, Integer.parseInt(s.substring(0, s.indexOf(" ")))));
                        if ("heart".equals(s.substring(s.lastIndexOf(" ") + 1, s.length()).toLowerCase()))
                            ch.writeAndFlush(new Packet(new Card(null, CardsColors.HEART), null, Integer.parseInt(s.substring(0, s.indexOf(" ")))));
                        if ("spike".equals(s.substring(s.lastIndexOf(" ") + 1, s.length()).toLowerCase()))
                            ch.writeAndFlush(new Packet(new Card(null, CardsColors.SPIKE), null, Integer.parseInt(s.substring(0, s.indexOf(" ")))));
                    }
        }
        else
            System.out.println("You must call something like \"90 TILE\"");
        return 0;
    }

    private static void playCard(String line, Channel ch) {
        if (!line.contains(" ")) {
            System.out.println("You have to type \"numberOfPointsToAchieved strongColor\" example : \"90 heart\"");
            return ;
        }
        String card = line.substring(line.indexOf(" ") + 1, line.length());
        System.out.println(card);
        if ((card.matches("^-?\\d+$") && Integer.parseInt(card) <= hand.size()) && Integer.parseInt(card) > 0) {
            System.out.println("it's send !");
            ch.writeAndFlush(new Packet(hand.get(Integer.parseInt(card) - 1), null, 0));
            hand.remove(Integer.parseInt(card) - 1);
            get_turn = false;
        }
    }
}