package com.palakendra.palakendra.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("!test")
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserDetailsService uds;

    public JwtAuthFilter(JwtService jwt, UserDetailsService uds) {
        this.jwt = jwt; this.uds = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res); return;
        }
        String token = header.substring(7);

        try {
            // 1) Extract subject (username) from token (also verifies signature)
            String username = jwt.getUsername(token); // subject

            // 2) If no auth yet, load user and validate token (expiry + subject match)
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails ud = uds.loadUserByUsername(username);
                if (jwt.isTokenValid(token, ud)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            chain.doFilter(req, res);
        } catch (ExpiredJwtException e) {
            // Clear context and return a clean 401 for expired tokens
            SecurityContextHolder.clearContext();
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"success\":false,\"error\":\"Unauthorized\",\"reason\":\"Token expired\"}");
        } catch (Exception e) {
            // Invalid token: let the rest of the chain handle it (will become 401 on protected routes)
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
        }
    }
}
