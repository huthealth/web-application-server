package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));


            String header = br.readLine();
            if(header == null ) {
                log.debug("header is null");
                return;
            }
            String[] token = header.split(" ");
            String method = token[0];
            String url = token[1];
            int contextLen = 0;
            boolean logginCookie = false;

            while(!"".equals(header)) {
                if(header == null) break;
                log.debug(header);
                token = header.split(" ");
                if(token[0].equals("Content-Length:")) contextLen = Integer.parseInt(token[1]);
                if(token[0].equals("Cookie:")) {
                    if(token[1].equals("logined=true")) logginCookie = true;
                }
                header = br.readLine();
            }



            if(url.length() >11  && url.substring(0,12).matches("/user/create")) {
                if(method.equals("POST")) {
                    //br.readLine();
                    String httpBody = util.IOUtils.readData(br,contextLen);
                    createUser(httpBody);
                }
                else {
                    String[] temp = url.split("\\?");
                    url = temp[0];
                    createUser(temp[1]);
                }
            }

            boolean foundUser = false;
            if(method.equals("POST") && url.equals("/user/login")) {
                String httpBody = util.IOUtils.readData(br,contextLen);
                System.out.println(httpBody);
                Map<String,String> paramMap = util.HttpRequestUtils.parseQueryString(httpBody);
                String userId = paramMap.get("userId");
                String password = paramMap.get("password");
                User loginUser = DataBase.findUserById(userId);
                if(loginUser != null && loginUser.getPassword().equals(password)) {
                    foundUser = true;
                }
            }



            if(url.equals("/user/create") ) {
                String location = "http://localhost:8080/index.html";
                response302Header(dos,location,-1);
            }
            else if (url.equals("/user/login")) {
                if(foundUser) {
                    String location = "http://localhost:8080/index.html";
                    response302Header(dos,location,1);
                }
                else {
                    String location = "http://localhost:8080/user/login_failed.html";
                    response302Header(dos,location,0);
                }
            }
            else if( url.endsWith(".css")){
                byte[] body = getHtmlFile(url);
                response200HeaderWithCSS(dos, body.length);
                responseBody(dos, body);
            }
            else {
                byte[] body = getHtmlFile(url);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
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
		System.out.println();
		System.out.println("Cant find file path : "+ file.toPath());
		System.out.println();
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
                dos.writeBytes("Set-Cookie: logined=true");
            }
            else if (foundUser == 0) {
                dos.writeBytes("Set-Cookie: logined=false");
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
