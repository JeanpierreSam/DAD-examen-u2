package pe.edu.upeu.ms_gestion_alumno.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlumnoDTO {
    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    @Min(value = 1, message = "El ciclo debe ser al menos 1")
    @Max(value = 10, message = "El ciclo debe ser como maximo 10")
    private Integer ciclo;

    private Boolean estado;
    private Integer talleresInscritos;
}
