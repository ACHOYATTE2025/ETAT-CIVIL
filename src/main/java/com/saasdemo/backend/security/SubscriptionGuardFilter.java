package com.saasdemo.backend.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.entity.Utilisateur;
import com.saasdemo.backend.repository.SubscriptionRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionGuardFilter extends OncePerRequestFilter {

     private final SubscriptionRepository subscriptionRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Routes à exclure de la vérification d’abonnement
    private static final String[] WHITELIST = {
        "/authentification/**",
        "/public/**",
        "/actuator",
        "/api/etatcivil/v1/souscriptions/**",
        "/swagger-ui",
        "/swagger-ui/",
        "/swagger-ui/index.html",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/api/etatcivil/v1/swagger-ui/**",
        "/api/etatcivil/v1/v3/api-docs/**"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI().toLowerCase();

        // ✅ Vérifie avec AntPathMatcher au lieu de contains
        for (String exclude : WHITELIST) {
            if (pathMatcher.match(exclude, path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


         // ⚠️ Si pas d'authentification, ne pas tenter getPrincipal()
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Utilisateur usex && usex.getId() != null) {
            Optional<Subscription> subscription = subscriptionRepository.findActiveByCommune(usex.getCommune());

            if (subscription.isEmpty()) {
               log.warn("❌ Aucun abonnement actif trouvé pour la commune {} de l’utilisateur {}",
            usex.getCommune().getNameCommune(), usex.getUsername());
            response.sendError(HttpServletResponse.SC_PAYMENT_REQUIRED,
            "ABONNEMENT INACTIF OU EXPIRE");
    return;
            }else {
            log.info("✅ Abonnement valide trouvé : {}", subscription.get().getStatus());
        }

        filterChain.doFilter(request, response);
    }

  }

}
