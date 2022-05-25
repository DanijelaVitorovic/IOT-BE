package com.dex.coreserver.security;

import com.dex.coreserver.exceptions.AccountLockedResponse;
import com.dex.coreserver.exceptions.InvalidLoginResponse;
import com.google.gson.Gson;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

        if (e instanceof LockedException) {
            AccountLockedResponse r = new AccountLockedResponse();
            String res = new Gson().toJson(r);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(401);
            httpServletResponse.getWriter().print(res);
            return;
        }

        InvalidLoginResponse loginResponse = new InvalidLoginResponse();
        String jsonLoginResponse = new Gson().toJson(loginResponse);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(400);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().print(jsonLoginResponse);
    }
}
