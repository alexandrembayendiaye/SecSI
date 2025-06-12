package com.medical.GestionDossierMedical.controller;

import com.medical.GestionDossierMedical.dto.RegisterRequest;
import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.repository.UserRepository;
import com.medical.GestionDossierMedical.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil ;
    @Autowired
    private UserRepository userRepository ;
    @Autowired
    private PasswordEncoder passwordEncoder ;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData)
    {
        try
        {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username,password)
            );
            String token  = jwtUtil.generateToken(username);
             return ResponseEntity.ok(Map.of("token",token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Nom d'utilisateur ou mot de pass incorredte");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest)
    {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent())
        {
            return ResponseEntity.badRequest().body("Nom d'utilisateur deja utilise ");
        }
        User user  = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(registerRequest.getRoles());
        userRepository.save(user);
        ResponseEntity.ok(user);
        return ResponseEntity.ok("utilisateur enregistrer avec succes!") ;

    }
}
