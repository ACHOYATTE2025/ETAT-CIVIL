package com.saasdemo.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saasdemo.backend.repository.UtilisateurRepository;
import com.saasdemo.backend.service.UtilisateurService;
import com.saasdemo.backend.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter  {

  private final JwtUtil jwtUtil;
  private final UtilisateurRepository userRepository;
  private final UtilisateurService utilisateurService;



  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

            HttpServletRequest req = (HttpServletRequest) request;
            String header = req.getHeader("Authorization");
    
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Claims claims = jwtUtil.extractAllClaims(token);
                    String email = claims.getSubject();
                    Long organizationId = claims.get("organizationId", Long.class);
    
                    UserDetails userDetails = this.utilisateurService.loadUserByUsername(email);
                        userRepository.findByEmail(email).ifPresent(user -> {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, userDetails.getAuthorities()  );
                        SecurityContextHolder.getContext().setAuthentication(auth);
    
                        TenantContext.setCurrentTenantId(organizationId); // multi-tenant context
                    });
                } catch (Exception ex) {
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
            }
    
            try {
                chain.doFilter(request, response);
            } finally {
                TenantContext.clear(); // évite les fuites de thread
            }
        }

   
  }











 /* @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                String email = claims.getSubject();
                Long organizationId = claims.get("organizationId", Long.class);

                userRepository.findByEmail(email).ifPresent(user -> {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    TenantContext.setCurrentTenantId(organizationId); // multi-tenant context
                });
            } catch (Exception ex) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // évite les fuites de thread
        }
    }*/


 
   
  

    
