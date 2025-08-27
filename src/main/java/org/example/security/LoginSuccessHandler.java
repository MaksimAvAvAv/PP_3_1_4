package org.example.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = Logger.getLogger(LoginSuccessHandler.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Создаем JSON response с информацией о пользователе
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Login successful");
        responseData.put("username", authentication.getName());
        responseData.put("redirectUrl", determineRedirectUrl(authentication));

        // Добавляем роли пользователя
        responseData.put("roles", authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toArray());

        String jsonResponse = objectMapper.writeValueAsString(responseData);
        response.getWriter().write(jsonResponse);

        logger.info("User " + authentication.getName() + " logged in successfully");
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