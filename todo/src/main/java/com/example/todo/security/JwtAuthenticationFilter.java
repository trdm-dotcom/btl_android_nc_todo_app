package com.example.todo.security;

import com.example.todo.models.dto.DataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtilities jwtUtilities;
    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = this.parseJwt(request);
            if (token != null && this.jwtUtilities.validateToken(token)) {
                DataRequest dataRequest = null;
                try {
                    dataRequest = this.jwtUtilities.getDataRequest(token);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(dataRequest.getUd().getUsername());
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                    log.info("authenticated user with username:{}", dataRequest.getUd().getUsername());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
        }
        catch (Exception e) {
            log.error("Cannot set user authentication: ", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("jwt ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
