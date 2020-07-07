package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.util.Collection;
import java.util.Map;

import model.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import db.DataBase;

public class ListUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        log.debug("sessionId in cookie : {}", request.getCookies().getCookie("JSESSION"));
        if (!isLogin(request.getSession())) {
            response.sendRedirect("/user/login.html");
            return;
        }

        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }

    private boolean isLogin(HttpSession session) {
        log.debug("listUserController sessionId : {}",session.getId());
        User user = (User)session.getAttribute("user");

        if (user == null) {
            return false;
        }
        return true;
    }
}
