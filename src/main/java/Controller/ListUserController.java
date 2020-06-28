package Controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

import java.util.Collection;
import java.util.Map;

public class ListUserController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
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
}
