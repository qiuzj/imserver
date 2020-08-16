package cn.javaee.im.command;

import cn.javaee.im.room.ChatRoom;

import java.util.Collection;

public class ChatRoomListCommand extends BaseCommand {
    private Collection<ChatRoom> roomList;

    public ChatRoomListCommand(Collection<ChatRoom> roomList) {
        setType(Command.LIST_CHAT_ROOM);
        this.roomList = roomList;
    }

    public Collection<ChatRoom> getRoomList() {
        return roomList;
    }

    public void setRoomList(Collection<ChatRoom> roomList) {
        this.roomList = roomList;
    }
}
