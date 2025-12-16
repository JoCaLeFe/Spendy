package com.spendy.backend.controller.auth;

import com.spendy.backend.dto.auth.AuthLoginDTO;
import com.spendy.backend.dto.auth.AuthRegisterDTO;
import com.spendy.backend.dto.auth.AuthTokenDTO;
import com.spendy.backend.model.User;
import com.spendy.backend.repository.UserRepository;
import com.spendy.backend.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

import static com.spendy.backend.configuration.ApiPaths.V1;

@RestController
@RequestMapping(V1 + "/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserRepository userRepo, JwtService jwtService, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterDTO dto) {
        if (userRepo.findByEmailIgnoreCase(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
        }

        User user = new User(null, dto.getEmail(), encoder.encode(dto.getPassword()), Set.of("USER"));
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Usuario registrado"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginDTO dto) {
        User user = userRepo.findByEmailIgnoreCase(dto.getEmail())
                .orElse(null);

        if (user == null || !encoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
        return ResponseEntity.ok(new AuthTokenDTO(token));
    }
}