package org.example.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = Logger.getLogger(LoginSuccessHandler.class.getName());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = determineRedirectUrl(authentication);
        logger.info("User " + authentication.getName() + " logged in successfully, redirecting to: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "/admin";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return "/user";
        } else {
            return "/";
        }
    }
}