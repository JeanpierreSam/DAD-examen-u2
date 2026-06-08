package pe.edu.upeu.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.upeu.api_gateway.security.JwtValidator;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationGlobalFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/api/auth/"
    );

    public AuthenticationGlobalFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (esPublica(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            enviarError(response, HttpStatus.UNAUTHORIZED, "Falta el token (Authorization: Bearer)");
            return;
        }
        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtValidator.validarYObtenerClaims(token);
        } catch (JwtException e) {
            enviarError(response, HttpStatus.UNAUTHORIZED, "Token JWT invalido o expirado");
            return;
        }

        String username = claims.getSubject();
        String rol = String.valueOf(claims.get("rol"));

        if (!autorizado(method, path, rol)) {
            enviarError(response, HttpStatus.FORBIDDEN,
                    "El rol " + rol + " no tiene permiso para " + method + " " + path);
            return;
        }

        request.setAttribute("X-Auth-User", username);
        request.setAttribute("X-Auth-Rol", rol);

        filterChain.doFilter(request, response);
    }

    private boolean esPublica(String path) {
        return RUTAS_PUBLICAS.stream().anyMatch(path::startsWith);
    }

    private boolean autorizado(String method, String path, String rol) {
        if ("ADMIN".equals(rol)) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }
        if ("POST".equalsIgnoreCase(method)
                && (path.matches("/api/talleres/\\d+/inscribir-alumno/\\d+")
                || path.matches("/api/talleres/\\d+/matricular-alumno/\\d+"))) {
            return "ALUMNO".equals(rol);
        }
        return false;
    }

    private void enviarError(HttpServletResponse response, HttpStatus status, String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), mensaje);
        response.getWriter().write(body);
        response.getWriter().flush();
    }
}
