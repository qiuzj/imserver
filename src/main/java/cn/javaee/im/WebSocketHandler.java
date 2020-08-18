package cn.javaee.im;

import cn.javaee.im.command.ChatRoomListCommand;
import cn.javaee.im.command.Command;
import cn.javaee.im.command.OnlineCommand;
import cn.javaee.im.room.ChatRoomManager;
import cn.javaee.im.room.OnlineManager;
import cn.javaee.im.util.AttributeUtils;
import cn.javaee.im.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    public static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    public static final ChannelGroup allChannelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String messageString = ((TextWebSocketFrame) frame).text();
            LOGGER.info("[channelRead0] received message: {}", messageString);
            Map<String, Object> messageMap = JsonUtils.parseObject(messageString, new TypeReference<Map<String, Object>>() {});

            String username = null;
            String roomName = null;

            switch ((Integer) messageMap.get("type")) {
                case 0: // 登录
                    username = (String) messageMap.get("username");
                    LOGGER.info("[LoginRequest] user: {}", username);

                    // 当前Channel加入通道组
                    allChannelGroup.add(ctx.channel());
                    // Channel关联用户名
                    AttributeUtils.setUsername(ctx.channel(), username);

                    // 请求响应
                    ctx.writeAndFlush(new TextWebSocketFrame(messageString));
                    LOGGER.info("[LoginResponse] message: {}", messageString);

                    // 推送聊天室列表
                    ChatRoomListCommand command = new ChatRoomListCommand(ChatRoomManager.getInstance().list());
                    String msg = JsonUtils.toJSONString(command);
                    ctx.writeAndFlush(new TextWebSocketFrame(msg));
                    break;
                case 1: // 客户端发送消息
                    roomName = (String) messageMap.get("roomName");
                    String message = (String) messageMap.get("message");
                    LOGGER.info("[MessageRequest] roomName: {}, content: {}", roomName, message);

                    messageMap.put("sender", AttributeUtils.getUsername(ctx.channel()));
                    String jsonString = JsonUtils.toJSONString(messageMap);
                    ChatRoomManager.getInstance().getChannelGroup(roomName).writeAndFlush(new TextWebSocketFrame(jsonString));
                    LOGGER.info("[MessageBroadcast] message: {}", jsonString);
                    break;
                case Command.JOIN_CHAT_ROOM: // 加入聊天室
                    roomName = (String) messageMap.get("roomName");
                    LOGGER.info("[JoinChatRoomRequest] roomName: {}", roomName);

                    // 加入聊天室
                    ChatRoomManager.getInstance().join(ctx.channel(), roomName);
                    // 加入用户在线列表
                    OnlineManager.getInstance().online(ctx.channel(), roomName);
                    // Channel关联聊天室
                    AttributeUtils.setRoomName(ctx.channel(), roomName);

                    // 请求响应
                    ctx.writeAndFlush(new TextWebSocketFrame(messageString));
                    LOGGER.info("[JoinChatRoomResponse] message: {}", messageString);

                    // 用户进出聊天室信息广播
                    changeNotify(roomName);
                    break;
                case Command.EXIT_CHAT_ROOM: // 退出聊天室
                    roomName = (String) messageMap.get("roomName");
                    LOGGER.info("[ExitChatRoomRequest] roomName: {}", roomName);

                    // 退出聊天室
                    ChatRoomManager.getInstance().exit(ctx.channel(), roomName);
                    // 退出用户在线列表
                    OnlineManager.getInstance().offline(ctx.channel(), roomName);
                    // 清空Channel关联聊天室
                    AttributeUtils.setRoomName(ctx.channel(), null);

                    // 请求响应
                    ctx.writeAndFlush(new TextWebSocketFrame(messageString));
                    LOGGER.info("[ExitChatRoomResponse] message: {}", messageString);

                    // 用户进出聊天室信息广播
                    changeNotify(roomName);
                    break;
                default:
                    break;
            }
        } else {
            LOGGER.error("not support frame type: {}", frame);
        }
    }

    private void changeNotify(String roomName) {
        // 在线用户列表更新通知
        onlineChangeNotify(roomName);
        // 聊天室列表更新通知
        roomListChangeNotify();
    }

    /**
     * 聊天室列表更新通知
     */
    private void roomListChangeNotify() {
        ChatRoomListCommand command = new ChatRoomListCommand(ChatRoomManager.getInstance().list());
        String msg = JsonUtils.toJSONString(command);
        allChannelGroup.writeAndFlush(new TextWebSocketFrame(msg));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[channelActive] remote address is {} ", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[channelClosed] remote address is {} ", ctx.channel().remoteAddress());
        logout(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("process error. user is {},  channel info {}",
                AttributeUtils.getUsername(ctx.channel()), ctx.channel(), cause);
        logout(ctx);
        ctx.channel().close();
    }

    /**
     * 用户退出或离线
     *
     * @param ctx
     */
    private void logout(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
//        String username = AttributeUtils.getUsername(channel);
        String roomName = AttributeUtils.getRoomName(channel);

        // 当前Channel退出通道组
        allChannelGroup.remove(ctx.channel());
        // 退出聊天室
        ChatRoomManager.getInstance().exit(channel, roomName);
        // 退出用户在线列表
        OnlineManager.getInstance().offline(channel, roomName);
        // 在线用户列表更新通知
        onlineChangeNotify(roomName);
    }

    /**
     * 在线用户列表更新通知
     */
    private void onlineChangeNotify(String roomName) {
        OnlineCommand command = new OnlineCommand(roomName, OnlineManager.getInstance().all(roomName));

        ChannelGroup channelGroup = ChatRoomManager.getInstance().getChannelGroup(roomName);
        channelGroup.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJSONString(command)));
    }
}
