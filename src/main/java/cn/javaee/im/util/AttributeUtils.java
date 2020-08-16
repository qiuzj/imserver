package cn.javaee.im.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class AttributeUtils {

    private static final AttributeKey<String> USERNAME = AttributeKey.newInstance("username");

    public static void setUsername(Channel channel, String username) {
        channel.attr(USERNAME).set(username);
    }

    public static void setUsername(ChannelHandlerContext ctx, String username) {
        setUsername(ctx.channel(), username);
    }

    public static String getUsername(Channel channel) {
        return channel.attr(USERNAME).get();
    }

    public static String getUsername(ChannelHandlerContext ctx) {
        return getUsername(ctx.channel());
    }
}
