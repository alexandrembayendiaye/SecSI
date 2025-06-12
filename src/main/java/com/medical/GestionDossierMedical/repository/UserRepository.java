package com.medical.GestionDossierMedical.repository;

import com.medical.GestionDossierMedical.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // méthode JPA générée automatiquement
}
