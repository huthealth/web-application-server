package webserver;


import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private BufferedReader br;

    private Map<String, String> headerMap = new HashMap<>();
    private Map<String,String> parameterMap = new HashMap<>();

    private String method = ""; //GET,POST만 존재한다고 가정
    private String path = "";
    private String body = "";
    //private String cookie = "";


    public HttpRequest(InputStream in) throws IOException {
        br = new BufferedReader(new InputStreamReader(in));
        String header = br.readLine();
        if(header == null ) {
            return;
        }
        String[] token = header.split(" ");
        method = token[0];
        String url = token[1];

        if(method.equals("GET")) {
            int indexOfQ = url.indexOf("?");
            path = url.substring(0,indexOfQ);
            String query = url.substring(indexOfQ+1);
            parameterMap = HttpRequestUtils.parseQueryString(query);
        }

        header = br.readLine();
        while(!"".equals(header)) {
            if(header == null) break;
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(header);
            headerMap.put(pair.getKey(),pair.getValue());
            header = br.readLine();
        }

        if(method.equals("POST")) {
            path = token[1];
            int contextLen = Integer.parseInt(headerMap.get("Content-Length"));
            body = util.IOUtils.readData(br,contextLen);
        }
    }


    public String getHeader(String fieldName){
        return headerMap.get(fieldName);
    }

    public String getMethod(){
        return  method;
    }


    public String getPath(){
        return path;
    }

    public String getBody(){
        return body;
    }

    public String getParameter(String name) {
        return parameterMap.get(name);
    }
}
