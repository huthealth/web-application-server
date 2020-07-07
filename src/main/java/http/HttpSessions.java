package http;

import model.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String id) {
        HttpSession session = sessions.get(id);
        if(session == null) {
            session = new HttpSession(id);
            sessions.put(id,session);
        }
        return session ;
    }

    public static void addSession(String sessionId, HttpSession session) {
        sessions.put(sessionId,session);
    }

    public static void removeSession(String sessionId){
        sessions.remove(sessionId);
    }

    public static int getSessionSize(){ return sessions.size();}
}
