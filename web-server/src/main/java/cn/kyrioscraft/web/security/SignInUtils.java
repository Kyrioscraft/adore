package cn.kyrioscraft.web.security;

import cn.kyrioscraft.data.model.CurrentUser;
import cn.kyrioscraft.data.model.User;
import cn.kyrioscraft.web.controller.GlobalController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionData;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by qk on 2017/3/7.
 */
public class SignInUtils {
    public static void authorizeUser(User user) {
        CurrentUser currentUser = new CurrentUser(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, user.getPassword(),user.getAuthorities()));
    }

    public static void setUserConnection(WebRequest request, ConnectionData connectionData) {
        String attribute = GlobalController.SESSION_USER_CONNECTION;
        request.setAttribute(attribute,connectionData, RequestAttributes.SCOPE_SESSION);
    }
}
