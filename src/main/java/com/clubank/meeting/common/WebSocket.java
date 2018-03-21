package com.clubank.meeting.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.clubank.meeting.entity.Message;
import com.clubank.meeting.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ServerEndpoint("/webSocket/{userId}")
@Component
@Slf4j
public class WebSocket {

    //静态变量，用来记录当前在线连接数。
    private static AtomicInteger onlineCount = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //用户id
    private Long userId;

    /**
     * 连接建立成功调用的方法
     *
     * @param userId  当前用户id
     * @param session 当前用户session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) {
        this.session = session;
        this.userId = userId;
        webSocketSet.add(this);
        addOnlineCount();
        log.info("有新连接加入sessionID:{}，当前连接数为:{}", session.getId(), webSocketSet.size());
        // 查询未读消息
//        log.info("spring上下文:{}", ApplicationContextRegister.getApplicationContext());
//        List<Message> messageList = ApplicationContextRegister.getApplicationContext().getBean(MessageService.class).findMessageList(0, userId);
//        if (!CollectionUtils.isEmpty(messageList)) {
//            for (Message message : messageList) {
//                sendMessage(JSON.toJSONString(message, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue));
//            }
//        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        log.info("有一条连接关闭，当前链接数为:{}", webSocketSet.size());
    }

    /**
     * 收到客户端消息
     *
     * @param message 客户端发送过来的消息
     * @param session 当前会话session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("客户端sessionID->{},发来的消息:{}", session.getId(), message);
        sendMessage(message);
    }

    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("sessionId->{}的webSocket连接发生错误!", session.getId(), error);
        error.printStackTrace();
    }

    /**
     * 给所有客户端群发消息
     *
     * @param message 消息内容
     * @throws IOException
     */
    public void sendMessageToAll(String message) {
        for (WebSocket webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(message);
                log.info("成功发送一条消息:{}", message);
            } catch (IOException e) {
                log.error("消息推送异常", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 给指定客户端发消息
     *
     * @param message
     * @param userIdList
     */
    public void sendMessageToSpecial(String message, List<Long> userIdList) {
        if (webSocketSet.size() > 0) {
            for (WebSocket webSocket : webSocketSet) {
                // 去重
                userIdList = userIdList.stream().distinct().collect(Collectors.toList());
                for (Long userId : userIdList) {
                    if (webSocket.userId == userId) {
                        try {
                            webSocket.session.getBasicRemote().sendText(message);
                            log.info("成功向指定人发送消息:{}", message);
                        } catch (IOException e) {
                            log.error("消息推送异常", e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            log.info("成功发送一条消息:{}", message);
        } catch (IOException e) {
            log.error("消息推送异常", e);
            e.printStackTrace();
        }
    }

    public static AtomicInteger getOnlineCount() {
        return onlineCount;
    }

    public static void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineCount.decrementAndGet();
    }
}
