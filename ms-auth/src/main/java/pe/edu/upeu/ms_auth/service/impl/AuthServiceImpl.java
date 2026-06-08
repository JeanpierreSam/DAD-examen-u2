package pe.edu.upeu.ms_auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.ms_auth.dto.AuthResponse;
import pe.edu.upeu.ms_auth.dto.LoginRequest;
import pe.edu.upeu.ms_auth.dto.RegisterRequest;
import pe.edu.upeu.ms_auth.entity.Usuario;
import pe.edu.upeu.ms_auth.exception.BusinessException;
import pe.edu.upeu.ms_auth.exception.UnauthorizedException;
import pe.edu.upeu.ms_auth.repository.UsuarioRepository;
import pe.edu.upeu.ms_auth.security.JwtService;
import pe.edu.upeu.ms_auth.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UsuarioRepository repository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El username '" + request.getUsername() + "' ya esta registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setEstado(true);
        repository.save(usuario);
        return construirRespuesta(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));
        if (!usuario.getEstado()) {
            throw new UnauthorizedException("Usuario inactivo");
        }
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }
        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getUsername(), usuario.getRol().name(),
                jwtService.getExpirationSegundos());
    }
}
