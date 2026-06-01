package pe.edu.upeu.ms_gestion_taller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstructorDTO {
    private Long id;
    private String nombre;
    private String email;
    private String especialidad;
}
