package cn.javaee.im.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class AttributeUtils {

    private static final AttributeKey<String> USERNAME = AttributeKey.newInstance("username");
    private static final AttributeKey<String> ROOM_NAME = AttributeKey.newInstance("roomName");

    public static void setUsername(Channel channel, String username) {
        channel.attr(USERNAME).set(username);
    }

    public static String getUsername(Channel channel) {
        return channel.attr(USERNAME).get();
    }

    public static void setRoomName(Channel channel, String roomName) {
        channel.attr(ROOM_NAME).set(roomName);
    }

    public static String getRoomName(Channel channel) {
        return channel.attr(ROOM_NAME).get();
    }
}
