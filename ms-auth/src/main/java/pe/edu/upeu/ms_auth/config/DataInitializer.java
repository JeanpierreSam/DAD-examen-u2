package pe.edu.upeu.ms_auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.ms_auth.entity.Rol;
import pe.edu.upeu.ms_auth.entity.Usuario;
import pe.edu.upeu.ms_auth.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {
            crearSiNoExiste(repository, encoder, "admin", "admin123", Rol.ADMIN);
            crearSiNoExiste(repository, encoder, "instructor1", "inst123", Rol.INSTRUCTOR);
            crearSiNoExiste(repository, encoder, "alumno1", "alum123", Rol.ALUMNO);
        };
    }

    private void crearSiNoExiste(UsuarioRepository repo, PasswordEncoder encoder,
                                  String username, String password, Rol rol) {
        if (!repo.existsByUsername(username)) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(encoder.encode(password));
            u.setRol(rol);
            u.setEstado(true);
            repo.save(u);
            System.out.println("Usuario creado: " + username + " / Rol: " + rol);
        }
    }
}
