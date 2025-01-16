package org.unibl.etf.sni.auth;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.unibl.etf.sni.logging.SIEM;
import org.unibl.etf.sni.service.JwtService;
import org.unibl.etf.sni.service.UserService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;
    @Autowired
    private SIEM sIEM;

//    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
//        this.jwtService = jwtService;
//        this.userService = userService;
//    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("/api/auth/extend-token".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt;
        String username;
        if (!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            SIEM siem = new SIEM();
            siem.logMaliciousRequest(request.getRemoteAddr(), request.getHeader("User-Agent"), request.getRequestURI(),
                    "[SIEM] Malicious request detected - token expired.");

            // Respond with a status code or message indicating token expiration
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\n\t\"code\": \"Token expired. Use /api/auth/extend-token to extend your session\"\n}");
            return;
        }


        if (!username.isEmpty()
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userService.findByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
