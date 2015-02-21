package me.rbrickis.simpledb.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Getter;
import me.rbrickis.simpledb.server.handlers.SimpleDBChannelHandler;
import me.rbrickis.simpledb.server.protocol.ServerState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

/**
 * Created by Ryan on 2/20/2015
 * <p/>
 * Project: SimpleDB
 */
public class SimpleDBServer {

    Properties settings = new Properties();


    static ServerState currentState = ServerState.WRITABLE;

    // Settings
    @Getter
    static int port = 3405;
    @Getter
    boolean useAuth = false;
    @Getter
    String password = "changeMe";


    // Parses the settings, and creates the server details
    public SimpleDBServer() {
        File file = new File("settings.properties");

        try {
            if (!file.exists()) {
                file.createNewFile();
                settings.setProperty("port", "3405");
                settings.setProperty("use_auth", "false");
                settings.store(new FileOutputStream(file), "SimpleDB Configuration File");
                new SimpleDBServer();
                return;
            }
            FileReader reader = new FileReader(file);
            settings.load(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            useAuth = Boolean.parseBoolean(settings.getProperty("use_auth"));
        } catch (Exception ex) {
            ex.printStackTrace();
            useAuth = false;
        }
        if (useAuth) {
            if (!settings.containsKey("password") || settings.getProperty("password").isEmpty()) {
                settings.setProperty("password", "changeMe");
            } else {
                password = settings.getProperty("password");
            }
        }
        if (settings.containsKey("port")) {
            try {
                port = Integer.parseInt(settings.getProperty("port"));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                port = 3405;
            }
        }
    }

    public static void run() {
        // Boss group accepts the incoming connections
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker group handles the traffic.
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            // Group the EventLoops
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // set the channel type
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("Client " + socketChannel.remoteAddress().getHostString() + " connecting...");
                            socketChannel.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
                            socketChannel.pipeline().addLast("decoder", new StringDecoder());
                            socketChannel.pipeline().addLast("encoder", new StringEncoder());
                            socketChannel.pipeline().addLast("handler", new SimpleDBChannelHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("Started...");
            ChannelFuture future = bootstrap.bind(port).sync().channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String... args) {
        run();
    }

    public static ServerState getCurrentState() {
        return currentState;
    }
}
