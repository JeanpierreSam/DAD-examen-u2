package pe.edu.upeu.ms_gestion_taller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstructorDTO {
    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String email;
}
