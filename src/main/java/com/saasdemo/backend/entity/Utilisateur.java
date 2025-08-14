package com.saasdemo.backend.entity;


import java.util.Collection;
import java.util.Collections;

import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "utilisateur")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Utilisateur  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    @Valid
    private String username;

    @Column(nullable = false, unique = true)
    @Valid
    @NaturalId
    private String email;


    @NaturalId
    @Column(nullable = false)
    @Valid
    private String password;

   
    @OneToOne(cascade = CascadeType.ALL)
    private Role role;

    private Boolean active;

    @OneToOne()
    private Subscription subscription;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commune_id")
    private Area commune;// Référence à la commune


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("Role"+ this.role.getLibele()));
    }

    @Override
    public String getUsername() {
        return this.username; }

    @Override
    public String getPassword() {
        return this.password; }

    @Override
    public boolean isAccountNonExpired() {
        return this.active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.active;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}
