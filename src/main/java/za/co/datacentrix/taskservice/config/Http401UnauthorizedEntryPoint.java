package za.co.datacentrix.taskservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import za.co.datacentrix.taskservice.handlers.ErrorResponse;


import java.io.IOException;
import java.time.Instant;




@Component
@Slf4j
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Check if the request is for an endpoint that should be accessible without authentication
        if (shouldAllowWithoutAuth(request.getRequestURI())) {
            response.setStatus(HttpServletResponse.SC_OK); // Allow access
            return;
        }

        log.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .timestamp(Instant.now())
                .message(authException.getMessage())
                .path(request.getServletPath())
                .build();

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.writeValue(response.getOutputStream(), body);
    }

    private boolean shouldAllowWithoutAuth(String requestURI) {

        return requestURI.startsWith("/api/v1/auth/")
                || requestURI.startsWith("/v2/api-docs")
                || requestURI.startsWith("/swagger-ui")
                || requestURI.startsWith("/swagger-resources")
                || requestURI.startsWith("/forgotPassword/")
                || requestURI.startsWith("/register/")
                || requestURI.startsWith("/ws/");
    }
}
