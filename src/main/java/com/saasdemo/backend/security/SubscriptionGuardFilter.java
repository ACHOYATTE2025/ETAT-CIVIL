package com.saasdemo.backend.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.SubscriptionRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionGuardFilter extends OncePerRequestFilter {

    private final SubscriptionRepository subscriptionRepository;

     // Routes à exclure de la vérification d’abonnement
    private static final String[] WHITELIST = {
            "/auth", "/public", "/actuator", "/subscription/create"
    };

   

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            String path = request.getRequestURI();

            // Si la route fait partie de la whitelist → on laisse passer
            for (String exclude : WHITELIST) {
                if (path.startsWith(exclude)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
                    
            Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                if (principal instanceof Utilisateur usex &&  usex.getId() != null) {
                    Optional<Subscription> subscription = subscriptionRepository.findActiveByCommuneId(usex.getId());
        
                    if (subscription.isEmpty()) {
                        HttpServletResponse res = (HttpServletResponse) response;
                        res.sendError(HttpServletResponse.SC_PAYMENT_REQUIRED, "ABONNEMENT INACTIF OU EXPIRE");
                        return;
                    }
                    
    }    filterChain.doFilter(request, response);
}
}