package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private Map<String ,String> headerMap = new HashMap<>();

    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String name, String value) {
        headerMap.put(name,value);
    }

    public void forward(String path){ //response 200
        try{
            byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
            if (path.endsWith(".css")) {
                headerMap.put("Content-Type", "text/css");
            } else if (path.endsWith(".js")) {
                headerMap.put("Content-Type", "application/javascript");
            } else {
                headerMap.put("Content-Type", "text/html;charset=utf-8");
            }
            dos.writeBytes("HTTP/1.1 200 OK \r\n");

            for (Map.Entry<String, String> entry : headerMap.entrySet()){
                dos.writeBytes(entry.getKey()+": " + entry.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String body) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            headerMap.put("Content-Type", "text/html;charset=utf-8");
            for (Map.Entry<String, String> entry : headerMap.entrySet()){
                dos.writeBytes(entry.getKey()+": " + entry.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");
            dos.write(body.getBytes(), 0, body.length());
            dos.flush();
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String location) { //response 302
        headerMap.put("Location",location);
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            for (Map.Entry<String, String> entry : headerMap.entrySet()){
                dos.writeBytes(entry.getKey()+": " + entry.getValue()+ "\r\n");
            }
            dos.writeBytes("\r\n");
            //dos.flush(); 왜 body에서만 flush?
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
