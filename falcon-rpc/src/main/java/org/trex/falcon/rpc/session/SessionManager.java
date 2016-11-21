package org.trex.falcon.rpc.session;

import org.trex.falcon.common.URL;
import org.trex.falcon.rpc.model.Message;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    //在线会话
    private static final ConcurrentHashMap<String, Session> onlineSessions = new ConcurrentHashMap<>();

    /**
     * 加入
     */
    public static boolean addSession(URL providerUrl, Session session) {
        String key = providerUrl.toString();
        if (!onlineSessions.containsKey(key)) {
            boolean success = onlineSessions.putIfAbsent(key, session) == null ? true : false;
            return success;
        }
        return false;
    }

    /**
     * 移除
     */
    public static Session removeSession(URL providerUrl) {
        return onlineSessions.remove(providerUrl.toString());
    }


    /**
     * 获取
     */
    public static Session getSession(URL providerUrl) {
        Session session = onlineSessions.get(providerUrl.toString());
        return session;
    }

    /**
     * 发送消息
     */
    public static void sendMessage(URL providerUrl, Message message) {
        Session session = onlineSessions.get(providerUrl.toString());
        if (session != null && session.isConnected()) {
            session.write(message);
        }
    }
}
