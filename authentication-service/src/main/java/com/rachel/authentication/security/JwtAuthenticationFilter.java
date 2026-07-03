package com.rachel.authentication.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                // Jika header tidak ada atau format salah, request akan diteruskan.
                // SecurityConfig kemudian akan menolak karena endpoint membutuhkan
                // authenticated().
                if (header == null || !header.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String token = header.substring(7);

                try {
                        jwtUtil.validateToken(token);

                        String username = jwtUtil.extractUsername(token);
                        String email = jwtUtil.extractEmail(token);
                        String role = jwtUtil.extractRole(token);
                        Long userId = jwtUtil.extractUserId(token);

                        List<GrantedAuthority> authorities = List.of(
                                        new SimpleGrantedAuthority("ROLE_" + role));

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        authorities);

                        authentication.setDetails(
                                        new WebAuthenticationDetailsSource()
                                                        .buildDetails(request));

                        SecurityContextHolder.getContext()
                                        .setAuthentication(authentication);

                        // Simpan informasi tambahan yang dapat diakses di controller jika diperlukan
                        request.setAttribute("username", username);
                        request.setAttribute("email", email);
                        request.setAttribute("role", role);
                        request.setAttribute("userId", userId);

                } catch (ExpiredJwtException e) {
                        response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Token telah kedaluwarsa");
                        return;
                } catch (JwtException e) {
                        response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Token tidak valid");
                        return;
                }

                filterChain.doFilter(request, response);
        }
}