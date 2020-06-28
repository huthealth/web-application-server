package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

           String url = request.getPath();

            if("/user/create".equals(url) ) {
                createUser(request, response);
            }

            else if ("/user/login".equals(url)) {
                login(request, response);
            }

            else if("/user/list".equals(url)) {
                listUser(request, response);
            }
            else {
                response.forward(url);
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void listUser(HttpRequest request, HttpResponse response) {
        String cookie = request.getHeader("Cookie");
        Map<String, String> cookieMap = util.HttpRequestUtils.parseCookies(cookie);
        String isLogin = cookieMap.get("logined");
        if (isLogin != null) {
            if (isLogin.equals("true")) {
                StringBuilder sb = new StringBuilder();
                Collection<User> userCollection = DataBase.findAll();
                sb.append(
                        "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">" +
                                "<TABLE BORDER=1>\n" +
                                "<CAPTION>User List</CAPTION>\n" +
                                "<TR>\n" +
                                "    <TD>ID</TD>\n" +
                                "    <TD>NAME</TD>\n" +
                                "    <TD>EMAIL</TD>\n" +
                                "</TR>");
                for (User user : userCollection) {
                    sb.append("<TR>\n +" +
                            "    <TD>" + user.getUserId() + "</TD>\n +" +
                            "    <TD>" + user.getName() + "</TD>\n +" +
                            "    <TD>" + user.getEmail() + "</TD>\n +" +
                            "<TR>");
                }
                sb.append("</TABLE></html>");
                String table = sb.toString();
                response.forwardBody(table);
            }
        }
        else {
            response.sendRedirect("/user/login.html");
        }
    }

    private void login(HttpRequest request, HttpResponse response) {
        boolean foundUser = false;
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        User loginUser = DataBase.findUserById(userId);
        if(loginUser != null && loginUser.getPassword().equals(password)) {
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
        }
        else {
            response.addHeader("Set-Cookie", "logined=false");
            response.sendRedirect("/user/login_failed.html");
        }
    }

    private void createUser(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"),request.getParameter("password"),
                request.getParameter("name"),request.getParameter("email"));
        DataBase.addUser(user);
        String location = "/index.html";
        response.sendRedirect(location);
    }

}
