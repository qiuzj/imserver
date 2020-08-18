package cn.javaee.im.room;

import cn.javaee.im.util.AttributeUtils;
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

    /**
     * 聊天室Channel组，用于组内广播消息. 格式：<聊天室名称, ChannelGroup>
     */
    private ConcurrentMap<String, ChannelGroup> roomGroupMap = new ConcurrentHashMap<>();

    /**
     * 所有聊天室基础信息. 格式：<聊天室名称, 聊天室对象>
     */
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
        // 退出原来的聊天室
//        String oldRoomName = AttributeUtils.getRoomName(channel);
//        if (oldRoomName != null && roomGroupMap.containsKey(oldRoomName)) {
//            roomGroupMap.get(oldRoomName).remove(channel);
//            updateOnlineCount(oldRoomName);
//        }

        // 加入新的聊天室
        if (!roomGroupMap.containsKey(roomName)) {
            ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
            roomGroupMap.putIfAbsent(roomName, channelGroup);
        }
        roomGroupMap.get(roomName).add(channel);

        // 在线人数更新
        updateOnlineCount(roomName);
    }

    /**
     * 在线人数更新
     *
     * @param roomName
     */
    private void updateOnlineCount(String roomName) {
        if (roomMap.containsKey(roomName)) {
            ChatRoom room = roomMap.get(roomName);
            synchronized (room) {
                room.setOnlineNum(roomGroupMap.get(roomName).size());
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

        // 在线人数更新
        updateOnlineCount(roomName);
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
