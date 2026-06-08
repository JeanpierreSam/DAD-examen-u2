package pe.edu.upeu.ms_gestion_taller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlumnoDTO {
    private Long id;
    private String codigo;
    private String nombres;
    private String apellidos;
    private String email;
    private Integer ciclo;
    private Integer talleresInscritos;
}
