package model;

import http.HttpSessions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
    private String sessionId;
    private Map<String,Object> attributes;

    public HttpSession(){
        sessionId = UUID.randomUUID().toString();
        attributes = new HashMap<>();
    }

    public HttpSession(String id) {
        sessionId = id;
        attributes = new HashMap<>();
    }

    public void setAttribute(String name, Object attribute) {
        attributes.put(name,attribute);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        HttpSessions.removeSession(sessionId);
    }

    public String getId() { return sessionId;}
}
