package com.saasdemo.backend.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saasdemo.backend.entity.Subscription;
import com.saasdemo.backend.repository.SubscriptionRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionGuardFilter extends OncePerRequestFilter {
    private  SubscriptionRepository subscriptionRepository;

   

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                Long orgId = TenantContext.getCurrentTenantId();

                if (orgId != null) {
                    Optional<Subscription> subscription = subscriptionRepository.findActiveByCommuneId(orgId);
        
                    if (subscription.isEmpty()) {
                        HttpServletResponse res = (HttpServletResponse) response;
                        res.sendError(HttpServletResponse.SC_PAYMENT_REQUIRED, "ABONNEMENT INACTIF OU EXPIRE");
                        return;
                    }
                    
    }    filterChain.doFilter(request, response);
}
}