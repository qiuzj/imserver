package cn.javaee.im.room;

import cn.javaee.im.util.AttributeUtils;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 在线管理
 */
public class OnlineManager {

    private static final OnlineManager instance = new OnlineManager();

    /**
     * 聊天室在线用户. 格式：<聊天室名称, <用户名, 在线用户对象>>
     */
    private ConcurrentMap<String, ConcurrentMap<String, OnlineUser>> roomOnlineMap = new ConcurrentHashMap<>();

    public static OnlineManager getInstance() {
        return instance;
    }

    /**
     * 聊天室用户上线
     */
    public void online(Channel channel, String roomName) {
        String username = AttributeUtils.getUsername(channel);
//        String oldRoomName = AttributeUtils.getRoomName(channel);
        // 删除原聊天室的在线信息
//        if (oldRoomName != null && roomOnlineMap.containsKey(oldRoomName)) {
//            roomOnlineMap.get(oldRoomName).remove(username);
//        }

        // 增加新聊天室的在线信息
        if (!roomOnlineMap.containsKey(roomName)) {
            ConcurrentMap<String, OnlineUser> newMap = new ConcurrentHashMap<>();
            roomOnlineMap.putIfAbsent(roomName, newMap);
        }
        roomOnlineMap.get(roomName).put(username, new OnlineUser(username));
    }

    /**
     * 聊天室用户离线
     *
     * @param channel
     * @param roomName
     */
    public void offline(Channel channel, String roomName) {
        String username = AttributeUtils.getUsername(channel);
        if (roomOnlineMap.containsKey(roomName)) {
            roomOnlineMap.get(roomName).remove(username);
        }
    }

    /**
     * 获取聊天室所有在线用户
     *
     * @return
     */
    public Collection<OnlineUser> all(String roomName) {
        if (roomName == null || !roomOnlineMap.containsKey(roomName)) {
            return Collections.emptyList();
        }
        return roomOnlineMap.get(roomName).values();
    }
}
