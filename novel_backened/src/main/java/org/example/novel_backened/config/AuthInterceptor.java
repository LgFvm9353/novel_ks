package org.example.novel_backened.config;

import io.jsonwebtoken.Claims;
import org.example.novel_backened.util.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;

    public AuthInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Try to parse token if present
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                Claims claims = jwtUtils.parseToken(token);
                request.setAttribute("userId", claims.getSubject());
                request.setAttribute("role", claims.get("role"));
            } catch (Exception e) {
                // Token invalid or expired, ignore here, checks below will handle it
            }
        }

        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)) {
            if (path.startsWith("/api/novels") || path.startsWith("/api/categories")) {
                return true;
            }
        }
        
        // If userId is not set (no token or invalid token), reject protected routes
        if (request.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
