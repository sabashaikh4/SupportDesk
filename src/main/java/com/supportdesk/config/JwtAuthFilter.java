package com.supportdesk.config;

import com.supportdesk.service.JwtService;
import com.supportdesk.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. read Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. if no header or not Bearer → skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. extract token — remove "Bearer " prefix
        String token = authHeader.substring(7);

        // 4. extract email from token
        String email = jwtService.extractEmail(token);

        // 5. if email found and user not already authenticated
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. load user from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. validate token
            if (jwtService.isTokenValid(token, userDetails)) {

                // 8. create authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. set in SecurityContext — user is now authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. continue to next filter
        filterChain.doFilter(request, response);
    }
}