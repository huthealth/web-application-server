package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

import com.google.common.io.Files;
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
            String[] request = requestParser(br);

            byte[] body = getHtmlFile(request[0]);

            response200Header(dos,request[1] ,body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String[] requestParser(BufferedReader br) throws IOException {
        String[] ret = new String[2];
        String header = br.readLine();
        String[] token = header.split(" ");
        ret[0] = token[1];
        while(!"".equals(header)) {
            if(header == null) break;
            System.out.println(header);
            token = header.split(" ");
            if(token[0].equals("Accept:")) {
                String[] content = token[1].split(",");
                ret[1] = content[0];
            }
            header = br.readLine();
        }
        System.out.println(ret[0] + " " + ret[1]);
        return ret;
    }

    private byte[] getHtmlFile(String url ) throws IOException {
        File file = new File("C:\\Users\\u\\IdeaProjects\\web-application-server\\webapp" + url);
        if(!file.exists()) {
            return "wrong url".getBytes();
        }
        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    private void response200Header(DataOutputStream dos, String type, int lengthOfBodyContent) {
        try {

            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type:"+type+"\r\n");
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
