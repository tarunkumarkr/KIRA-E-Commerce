// user-service/src/main/java/.../security/InternalAccessFilter.java
package com.kira.userservice.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InternalAccessFilter implements Filter {

    private static final String INTERNAL_SECRET = "internal-secret-value"; // same as Feign header

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String remoteAddr = req.getRemoteAddr(); // e.g. 127.0.0.1 or container IP
        String internalHeader = req.getHeader("X-Internal-Call");

        // allow localhost (auth-service calling locally) OR allow if internal-header matches secret
        boolean isLocal = "127.0.0.1".equals(remoteAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr);
        boolean hasSecret = INTERNAL_SECRET.equals(internalHeader);

        if (isLocal || hasSecret) {
            chain.doFilter(request, response);
            return;
        }

        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access to user-service is restricted");
    }
}
