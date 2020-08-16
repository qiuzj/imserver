package cn.javaee.im.command;

import cn.javaee.im.room.OnlineUser;

import java.util.Collection;

public class OnlineCommand extends BaseCommand {
    private Collection<OnlineUser> userList;

    public OnlineCommand(Collection<OnlineUser> userList) {
        setType(Command.LIST_CHAT_ROOM_USER);
        this.userList = userList;
    }

    public Collection<OnlineUser> getUserList() {
        return userList;
    }

    public void setUserList(Collection<OnlineUser> userList) {
        this.userList = userList;
    }
}
