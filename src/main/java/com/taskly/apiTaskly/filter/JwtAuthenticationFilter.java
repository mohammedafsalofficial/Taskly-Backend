package com.taskly.apiTaskly.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskly.apiTaskly.exception.JwtTokenException;
import com.taskly.apiTaskly.exception.JwtTokenExpiredException;
import com.taskly.apiTaskly.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtService.isValidToken(token)) {
                    String username = jwtService.extractUsername(token);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, null);
                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (JwtTokenExpiredException e) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            } catch (JwtTokenException e) {
                sendErrorResponse(response, request, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            } catch (Exception e) {
                sendErrorResponse(response, request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication Failed.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpServletRequest request, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", status);
        errorBody.put("error", message);
        errorBody.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getWriter(), errorBody);
    }
}
