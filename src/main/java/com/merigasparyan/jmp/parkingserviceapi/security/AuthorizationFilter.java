package com.merigasparyan.jmp.parkingserviceapi.security;

import com.merigasparyan.jmp.parkingserviceapi.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.merigasparyan.jmp.parkingserviceapi.security.util.JwtUtil.AUTH_TYPE;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AUTH_TYPE)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(AUTH_TYPE.length()).trim();

            if (!jwtUtil.isVerified(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtUtil.getUsername(token);


            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("Loaded userDetails class: " + userDetails.getClass());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
