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

    private HttpRequest request;
    private HttpResponse response;


    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            request = new HttpRequest(in);
            response = new HttpResponse(out);

           String url = request.getPath();
           String method = request.getMethod();
           String body = request.getBody();

            if(url.equals("/user/create") ) {
                if (method.equals("POST")) {
                    createUser(body);
                }
                String location = "/index.html";
                response.sendRedirect(location);
            }

            else if (url.equals("/user/login")) {
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

            else if(url.equals("/user/list")) {
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


            else {
                response.forward(url);
            }
        }

        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean createUser(String param) {
        Map<String,String> paramMap = util.HttpRequestUtils.parseQueryString(param);
        String userId = paramMap.get("userId");
        String password = paramMap.get("password");
        String name = paramMap.get("name");
        String email = paramMap.get("email");

        if(userId == null || password == null || name == null || email == null) {
            log.debug("회원가입 실패");
            return false;
        }
        User newUser = new User(userId,password,name,email);
        DataBase.addUser(newUser);
        log.debug("회원가입 성공");
        return true;
    }

    private byte[] getHtmlFile(String url ) throws IOException {

	String path = "./webapp" + url;
	File file = new File(path);
        if(!file.exists()) {
		log.debug("cant find file : {}", path);
            return "wrong url".getBytes();
        }
        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type:text/html\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderWithCSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type:text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location, int foundUser) {
        try {

            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            if(foundUser == 1) {
                dos.writeBytes("Set-Cookie: logined=true; Path=/ \r\n");
            }
            else if (foundUser == 0) {
                dos.writeBytes("Set-Cookie: logined=false; Path=/\r\n");
            }
            dos.writeBytes("\r\n");
            dos.flush();
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }



    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
