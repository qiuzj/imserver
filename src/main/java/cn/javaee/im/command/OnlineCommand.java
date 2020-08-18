package cn.javaee.im.command;

import cn.javaee.im.room.OnlineUser;

import java.util.Collection;

public class OnlineCommand extends BaseCommand {

    private String roomName;
    private Collection<OnlineUser> userList;

    public OnlineCommand(String roomName, Collection<OnlineUser> userList) {
        setType(Command.LIST_CHAT_ROOM_USER);
        this.roomName = roomName;
        this.userList = userList;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Collection<OnlineUser> getUserList() {
        return userList;
    }

    public void setUserList(Collection<OnlineUser> userList) {
        this.userList = userList;
    }
}
