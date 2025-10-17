package com.saasdemo.backend.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "utilisateur")
public class Utilisateur extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // <-- Rendre unique
    @Valid
    private String username;

    @Column(nullable = false, unique = true)
    @Valid
    private String email;

    @Column(nullable = false)
    @Valid
    private String password;

    @ManyToOne(fetch = FetchType.EAGER) // <-- corrigé
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder.Default
    private Boolean active = false;

    @Builder.Default
    private Boolean connected = false;

    @OneToOne
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commune_id")
    private Area commune;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + this.role.getLibele().name())
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE.equals(this.active);
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(this.active);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.TRUE.equals(this.active);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.active);
    }


    
  public Utilisateur(String email, String password,Area commune, Role role) {
    this.email = email;
    this.password = password;
    this.commune = commune;
    this.role = role;   // important !
}

  
}
