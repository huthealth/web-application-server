package Controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

public class LoginController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
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
}
