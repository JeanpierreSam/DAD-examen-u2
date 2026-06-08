package pe.edu.upeu.ms_auth.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.ms_auth.dto.AuthResponse;
import pe.edu.upeu.ms_auth.dto.LoginRequest;
import pe.edu.upeu.ms_auth.dto.RegisterRequest;
import pe.edu.upeu.ms_auth.exception.UnauthorizedException;
import pe.edu.upeu.ms_auth.security.JwtService;
import pe.edu.upeu.ms_auth.service.AuthService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validar(@RequestHeader("Authorization") String authorization) {
        String token = extraerToken(authorization);
        Claims claims = jwtService.validarYObtenerClaims(token);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("valido", true);
        resultado.put("username", claims.getSubject());
        resultado.put("rol", claims.get("rol"));
        resultado.put("expira", claims.getExpiration());
        return ResponseEntity.ok(resultado);
    }

    private String extraerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Falta el header Authorization tipo Bearer");
        }
        return authorization.substring(7);
    }
}
