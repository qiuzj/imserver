package cn.javaee.im.room;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 聊天室管理
 */
public class ChatRoomManager {

    private static final ChatRoomManager instance = new ChatRoomManager();

    private ConcurrentMap<String, ChannelGroup> roomGroupMap = new ConcurrentHashMap<>();

    private ConcurrentMap<String, ChatRoom> roomMap = new ConcurrentHashMap<>();

    {
        roomMap.put("程序员之家", new ChatRoom("程序员之家"));
        roomMap.put("电商交流社区", new ChatRoom("电商交流社区"));
        roomMap.put("物联网知天下", new ChatRoom("物联网知天下"));
        roomMap.put("感悟人生", new ChatRoom("感悟人生"));
    }

    public static ChatRoomManager getInstance() {
        return instance;
    }

    /**
     * 加入聊天室
     *
     * @param channel
     * @param roomName
     */
    public void join(Channel channel, String roomName) {
        if (!roomGroupMap.containsKey(roomName)) {
            ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
            roomGroupMap.putIfAbsent(roomName, channelGroup);
        }
        roomGroupMap.get(roomName).add(channel);

        // 在线人数加1
        if (roomMap.containsKey(roomName)) {
            ChatRoom room = roomMap.get(roomName);
            synchronized (room) {
                room.setOnlineNum(room.getOnlineNum() + 1);
            }
        }
    }

    /**
     * 退出聊天室
     *
     * @param channel
     * @param roomName
     */
    public void exit(Channel channel, String roomName) {
        if (roomGroupMap.containsKey(roomName)) {
            roomGroupMap.get(roomName).remove(channel);
        }

        // 在线人数减1
        if (roomMap.containsKey(roomName)) {
            ChatRoom room = roomMap.get(roomName);
            synchronized (room) {
                room.setOnlineNum(room.getOnlineNum() - 1);
            }
        }
    }

    /**
     * 获取聊天室对应的ChannelGroup
     *
     * @param roomName
     * @return
     */
    public ChannelGroup getChannelGroup(String roomName) {
        return roomGroupMap.get(roomName);
    }

    /**
     * 获取所获聊天室
     *
     * @return
     */
    public Collection<ChatRoom> list() {
        return roomMap.values();
    }
}
