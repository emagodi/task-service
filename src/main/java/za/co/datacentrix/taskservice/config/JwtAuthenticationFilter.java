package za.co.datacentrix.taskservice.config;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import za.co.datacentrix.taskservice.service.JwtService;


import java.io.IOException;

/**
    * Our jwt class extends OnePerRequestFilter to be executed on every http request
    * We can also implement the Filter interface (jakarta EE), but Spring gives us a OncePerRequestFilter
        class that extends the GenericFilterBean, which also implements the Filter interface.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {


        String path = request.getRequestURI();

        log.info("Request URI: {}", path);

        if (path.startsWith("/v2/api-docs") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/webjars") ||
                path.startsWith("/swagger-ui.html") ||
                path.startsWith("/forgotPassword") ||
                path.startsWith("/register") ||
                path.startsWith("/ws")

        ) {

            filterChain.doFilter(request, response);
            return;
        }

        // Try to get JWT from cookie or Authorization Header
        String jwt = jwtService.getJwtFromCookies(request);
        final String authHeader = request.getHeader("Authorization");

        // Allow requests without JWT if they are for authentication
        if ((jwt == null && (authHeader == null || !authHeader.startsWith("Bearer "))) || path.contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // If the JWT is not in the cookies but in the "Authorization" header
        if (jwt == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // after "Bearer "
        }

        final String userEmail = jwtService.extractUserName(jwt);

        if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Update the Spring Security context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}



