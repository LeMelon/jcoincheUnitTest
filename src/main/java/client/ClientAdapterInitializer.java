package client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;

public class ClientAdapterInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public ClientAdapterInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(sslCtx.newHandler(channel.alloc(), Client.HOST, Client.PORT));
        pipeline.addLast(new ObjectDecoder((ClassResolvers.cacheDisabled(getClass().getClassLoader()))));
        pipeline.addLast(new ObjectEncoder());


        pipeline.addLast(new ClientAdapterHandler());
    }

}