package us.sportsanalytics.backend.security.jwt;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import us.sportsanalytics.backend.security.CustomUserDetailsService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Get everything starting at the 7th index - Skips 'Bearer ' 0-6

        try {

            UUID userId = UUID.fromString(jwtService.extractSubject(token)); // Parser in jwtService already verifies
            // the JWT signature

            UserDetails user = userDetailsService.loadUserById(userId);

            // Directly construct the Authentication object and put in SC
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            log.error("JWT authentication failed", e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);

    }
}
