package com.medical.GestionDossierMedical.security;

import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * filtre qui interceptera les requetes et validera le token JWT
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil ;
    @Autowired
    private UserRepository userRepository ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization") ;
        String token = null ;
        String username = null ;
        if(authHeader != null && authHeader.startsWith("Bearer "))
        {
            token  = authHeader.substring(7) ;
            username = jwtUtil.getUsernameFromToken(token);
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            User user = userRepository.findByUsername(username).orElse(null) ;
            if(user != null && jwtUtil.validateToken(token))
            {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                user.getRoles().stream()
                                        .map(role -> new SimpleGrantedAuthority( "ROLE_"+ role))
                                        .collect(Collectors.toList())
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
