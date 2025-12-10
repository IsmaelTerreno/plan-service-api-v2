package com.remotejob.planservice.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * JwtFilter is a servlet filter that intercepts HTTP requests to check for a valid JWT token in the Authorization header.
 * If a valid token is present, it extracts the claims, creates an authentication object, and sets it in the SecurityContextHolder
 * for subsequent security checks within the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;

    /**
     * This method filters incoming requests to determine if they contain a valid JWT token.
     * If a valid token is found, it extracts the claims, generates the authentication object,
     * and sets it in the SecurityContextHolder. Finally, it proceeds with the filter chain.
     * For public endpoints, the filter simply passes through without requiring authentication.
     *
     * @param request  the incoming request to be filtered
     * @param response the response associated with the request
     * @param fc       the filter chain to pass the request and response to the next filter in the chain
     * @throws IOException      if an I/O error occurs during the filtering process
     * @throws ServletException if an error occurs while processing the request or response
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        log.info("Processing request to: {} {}", method, requestURI);

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(method, requestURI)) {
            log.info("Public endpoint - skipping JWT validation");
            fc.doFilter(request, response);
            return;
        }

        final String token = getTokenFromRequest(httpRequest);

        if (token == null) {
            log.warn("No JWT token found in Authorization header");
        } else if (!jwtProvider.validateToken(token)) {
            log.warn("JWT token validation failed for request to: {}", requestURI);
        } else {
            final Claims claims = jwtProvider.getAccessClaims(token);
            final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            log.info("JWT authentication successful for user: {} with roles: {}",
                    jwtInfoToken.getUsername(), jwtInfoToken.getRoles());
        }
        fc.doFilter(request, response);
    }

    /**
     * Checks if the given request path and method correspond to a public endpoint that doesn't require authentication.
     *
     * @param method the HTTP method (GET, POST, etc.)
     * @param path the request URI path
     * @return true if the endpoint is public, false otherwise
     */
    private boolean isPublicEndpoint(String method, String path) {
        // OPTIONS requests are always public (CORS preflight)
        if ("OPTIONS".equals(method)) {
            return true;
        }

        // Public GET endpoints
        if ("GET".equals(method)) {
            return path.matches("/api/v1/plan/[^/]+") || // /api/v1/plan/{id}
                   path.matches("/api/v1/plan/user/[^/]+") || // /api/v1/plan/user/{userId}
                   path.startsWith("/actuator/health") ||
                   path.startsWith("/doc") ||
                   path.startsWith("/swagger-ui") ||
                   path.startsWith("/v3/api-docs") ||
                   path.startsWith("/api-docs");
        }

        return false;
    }

    /**
     * Extracts the JWT token from the HTTP request's Authorization header.
     * The token is expected to be prefixed with "Bearer ".
     *
     * @param request the HttpServletRequest containing the Authorization header
     * @return the extracted JWT token, or null if the Authorization header is missing or does not start with "Bearer "
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        return (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

}