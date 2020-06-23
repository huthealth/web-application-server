package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Map;

import com.google.common.io.Files;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

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
            String request = requestParser(br);

            byte[] body = getHtmlFile(request);

            response200Header(dos ,body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String requestParser(BufferedReader br) throws IOException {

        String header = br.readLine();
        String[] token = header.split(" ");
        String method = token[0];
        String url = token[1];
        int contextLen = 0;

        while(!"".equals(header)) {
            if(header == null) break;
            System.out.println(header);
            token = header.split(" ");
            if(token[0].equals("Content-Length:")) contextLen = Integer.parseInt(token[1]);
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
        return url;
    }

    private boolean createUser(String param) {
        Map<String,String> paramMap = util.HttpRequestUtils.parseQueryString(param);
        String userId = paramMap.get("userId");
        String password = paramMap.get("password");
        String name = paramMap.get("name");
        String email = paramMap.get("email");

        if(userId == null || password == null || name == null || email == null) {
            System.out.println("회원가입 실패");
            return false;
        }
        User newUser = new User(userId,password,name,email);
        System.out.println("회원가입 성공");
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
