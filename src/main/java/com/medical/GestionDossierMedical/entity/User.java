package com.medical.GestionDossierMedical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Setter
    @Column(unique = true, nullable = false)
    private String username ;
    @Setter
    @Column(nullable = false)
    private String password ;
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles; // deux roles de meme nom ne peuvent exister

    public User() {
    }

    public User(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }



}
