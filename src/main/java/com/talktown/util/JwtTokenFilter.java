package com.talktown.util;

import com.talktown.entity.Role;
import com.talktown.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;


public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                logger.debug("Valid JWT token found, setting authentication context");
                setAuthenticationContext(token, request, response);
            } else {
                logger.debug("No valid JWT token found");
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = getUserDetails(token);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(String token) {
        Claims claims = jwtTokenProvider.parseClaims(token);
        String username = claims.getSubject();
        Set<String> roles = new HashSet<>(claims.get("roles", List.class));

        User user = new User();
        user.setUsername(username);
        roles.forEach(role -> user.setRoles(new Role(role)));

        return user;
    }

}
