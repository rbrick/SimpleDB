package me.rbrickis.simpledb.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.rbrickis.simpledb.server.data.DataObject;
import me.rbrickis.simpledb.server.data.Database;

/**
 * Created by Ryan on 2/20/2015
 * <p/>
 * Project: SimpleDB
 */
public class SimpleDBChannelHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
          System.out.println("Message Received");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        String[] kv = message.split("=");
        DataObject object = new DataObject(kv[0], kv[1]);
        Database.insert(object);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
