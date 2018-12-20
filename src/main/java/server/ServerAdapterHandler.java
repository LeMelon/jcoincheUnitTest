package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerAdapterHandler extends SimpleChannelInboundHandler<Packet> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static final ChannelGroup players = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static int player = 0;
    static private Core core = new Core();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    public void operationComplete(Future<Channel> future) throws Exception {
                        channels.add(ctx.channel());
                        player += 1;
                        if (players.size() == 4)
                            ctx.writeAndFlush(new Packet(null, "There is no place left for players", 0));
                        else
                            ctx.writeAndFlush(new Packet(null, "type \"join\" to play !", 0));
                    }
                });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        core.initState();
        for (Channel p : channels) {
            if (p != ctx.channel()) {
                String str = "You can try to type \"join\" to join the room";
                p.writeAndFlush(new Packet(null, str, 0));
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet s) throws Exception
    {

        for (Channel i : channels) {
            if (i == ctx.channel()) {
                if ("join\r\n".equals(s.getS().toLowerCase())) {
                    if (!(players.size() < 4))
                    {
                        i.writeAndFlush(new Packet(null, "This room is full", 0));
                        return ;
                    }
                    players.add(i);
                    channels.remove(i);
                    String str = "Welcome, you are player " + player;
                    ctx.writeAndFlush(new Packet(null, str, 0));
                    i.writeAndFlush(new Packet(null, "You have joined", 0));
                    core.loop(players, s);
                    return ;
                }
                i.writeAndFlush(new Packet(null, "Type \"join\" to join the room", 0));
                return ;
            }
        }
        if (s.getCall() == 0 && s.getCard() == null)
        {
            for (Channel p : players) {
                if (p != ctx.channel()) {
                    String str = "[" + ctx.channel().remoteAddress() + "] " + s.getS();
                    p.writeAndFlush(new Packet(null,  str, 0));
                }
            }
        }
        else
            core.loop(players, s);
        if (s.getS() != null && "quit\r\n".equals(s.getS().toLowerCase()))
            ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}