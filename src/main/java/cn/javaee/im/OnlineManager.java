package cn.javaee.im;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 在线管理
 */
public class OnlineManager {

    private static final OnlineManager instance = new OnlineManager();

    private ConcurrentMap<String, OnlineUser> onlineUserMap = new ConcurrentHashMap<>();

    public static OnlineManager getInstance() {
        return instance;
    }

    /**
     * 用户在线
     *
     * @param user
     */
    public void online(OnlineUser user) {
        onlineUserMap.put(user.getUsername(), user);
    }

    /**
     * 用户离线
     *
     * @param username
     */
    public void offline(String username) {
        onlineUserMap.remove(username);
    }

    /**
     * 获取所有在线用户
     *
     * @return
     */
    public Collection<OnlineUser> all() {
        return onlineUserMap.values();
    }
}
