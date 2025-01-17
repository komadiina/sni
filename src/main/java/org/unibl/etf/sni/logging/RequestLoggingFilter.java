package org.unibl.etf.sni.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unibl.etf.sni.security.AccessController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class RequestLoggingFilter extends HttpFilter {
    private HashSet<String> whitelistEndpoints;

    @Autowired
    private SIEM siem;
    @Autowired
    private AccessController accessController;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        whitelistEndpoints = new HashSet<>();
        whitelistEndpoints.addAll(Arrays.asList(
                "/api/auth/login",
                "/api/auth/otp",
                "/api/auth/register",
                "/api/paypal",
                "/api/paypal/.*",
                "/api/paypal/order",
                "/api/paypal/order/.*",
                "/api/auth/.*"
        ));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String endpoint = request.getMethod() + " " + request.getRequestURI();

        siem.logRequest(ipAddress, userAgent, endpoint, "[SIEM] New request.");

        // check if user is trying to extend token
        if (endpoint.equals("/api/auth/extend-token")) {
            chain.doFilter(request, response);
            return;
        }

        // HAHSHAHAHHAHAHA JER SAM DODAO <METHOD> PREFIKS U ENDPOINT PA GA NE WHITELISTUJE ASDASDNAKJSDNASKJH IZGORIO
        if (endpoint.contains(" "))
            endpoint = endpoint.split(" ")[1];

        String reason = isMaliciousRequest(request);
        if (reason != null && !whitelistEndpoints.contains(endpoint) && !isWhitelisted(endpoint)) {
            siem.logMaliciousRequest(ipAddress, userAgent, endpoint, "[SIEM] " + reason);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        chain.doFilter(request, response);
    }

    public boolean isWhitelisted(String endpoint) {
        for (String wlEndpoint :
                whitelistEndpoints) {
            if (wlEndpoint.matches(endpoint)) return true;
        }

        return false;
    }

    @Override
    public void destroy() {
    }

    private String isMaliciousRequest(HttpServletRequest request) {
        // check for SQL injection patterns in query parameters
        // grabbed from somewhere idk where
        String query = request.getQueryString();
        if (query != null && query.matches(".*(['\";]+|(--)+).*")) {
            return "SQL injection attempt";
        }

        if (!accessController.isRequestAcceptable(request)) return "Unauthorized token usage";

        String token = AccessController.extractToken(request.getHeader("Authorization"));
        if (!accessController.notSpoofed(token, request.getHeader("User-Agent"), request.getRemoteAddr()))
            return "Malicious token usage";

        return null;
    }
}
