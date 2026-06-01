package pe.edu.upeu.ms_gestion_alumno.dto;

import lombok.*;

@Data
@Builder
public class AlumnoDTO {
    private Long id;
    private String nombre;
    private String email;
    private String grado;
}
