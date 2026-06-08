package pe.edu.upeu.ms_auth.service;

import pe.edu.upeu.ms_auth.dto.AuthResponse;
import pe.edu.upeu.ms_auth.dto.LoginRequest;
import pe.edu.upeu.ms_auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
