package cn.javaee.im.room;

/**
 * 聊天室
 */
public class ChatRoom {

    /** 房间名称 */
    private String name;
    /** 房间图标 */
    private String icon;
    /** 在线房间人数 */
    private int onlineNum;

    public ChatRoom(String name) {
        this.name = name;
        this.onlineNum = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        this.onlineNum = onlineNum;
    }
}
