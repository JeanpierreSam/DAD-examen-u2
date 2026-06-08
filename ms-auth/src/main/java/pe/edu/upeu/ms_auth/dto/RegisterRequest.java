package pe.edu.upeu.ms_auth.dto;

import jakarta.validation.constraints.NotBlank;
import pe.edu.upeu.ms_auth.entity.Rol;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Rol rol = Rol.ALUMNO;
}
