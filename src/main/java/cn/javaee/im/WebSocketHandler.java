package cn.javaee.im;

import cn.javaee.im.util.AttributeUtils;
import cn.javaee.im.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
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

    public static final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String messageString = ((TextWebSocketFrame) frame).text();
            LOGGER.info("[channelRead0] received message: {}", messageString);
            Map<String, Object> messageMap = JsonUtils.parseObject(messageString, new TypeReference<Map<String, Object>>() {});

            switch ((Integer) messageMap.get("type")) {
                case 0: // 登录
                    String username = (String) messageMap.get("username");
                    LOGGER.info("[LoginRequest] user: {}", username);
                    channelGroup.add(ctx.channel());
                    AttributeUtils.setUsername(ctx, username);

                    ctx.writeAndFlush(new TextWebSocketFrame(messageString));
                    LOGGER.info("[LoginResponse] message: {}", messageString);
                    break;
                case 1: // 客户端发送消息
                    String message = (String) messageMap.get("message");
                    LOGGER.info("[MessageRequest] message content: {}", message);

                    messageMap.put("sender", AttributeUtils.getUsername(ctx));
                    String jsonString = JsonUtils.toJSONString(messageMap);
                    channelGroup.writeAndFlush(new TextWebSocketFrame(jsonString));
                    LOGGER.info("[MessageBroadcast] message: {}", jsonString);
                    break;
                default:
                    break;
            }
        } else {
            LOGGER.error("not support frame type: {}", frame);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[channelActive] remote address is {} ", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[channelClosed] remote address is {} ", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("process error. user is {},  channel info {}", AttributeUtils.getUsername(ctx), ctx.channel(), cause);
        channelGroup.remove(ctx.channel());
        ctx.channel().close();
    }
}
