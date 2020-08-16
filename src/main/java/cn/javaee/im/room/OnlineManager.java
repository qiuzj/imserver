package cn.javaee.im.room;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 在线管理
 */
public class OnlineManager {

    private static final OnlineManager instance = new OnlineManager();

    private ConcurrentMap<String, ConcurrentMap<String, OnlineUser>> groupOnlineMap = new ConcurrentHashMap<>();

    public static OnlineManager getInstance() {
        return instance;
    }

    /**
     * 聊天室用户上线
     *
     * @param user
     */
    public void online(String roomName, OnlineUser user) {
        if (!groupOnlineMap.containsKey(roomName)) {
            ConcurrentMap<String, OnlineUser> newMap = new ConcurrentHashMap<>();
            groupOnlineMap.putIfAbsent(roomName, newMap);
        }
        groupOnlineMap.get(roomName).put(user.getUsername(), user);
    }

    /**
     * 聊天室用户离线
     *
     * @param username
     */
    public void offline(String roomName, String username) {
        if (groupOnlineMap.containsKey(roomName)) {
            groupOnlineMap.get(roomName).remove(username);
        }
    }

    /**
     * 获取聊天室所有在线用户
     *
     * @return
     */
    public Collection<OnlineUser> all(String roomName) {
        if (roomName == null || !groupOnlineMap.containsKey(roomName)) {
            return Collections.EMPTY_LIST;
        }
        return groupOnlineMap.get(roomName).values();
    }
}
