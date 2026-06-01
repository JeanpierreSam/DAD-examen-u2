package pe.edu.upeu.ms_gestion_taller.dto;

import pe.edu.upeu.ms_gestion_taller.entity.Taller;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TallerDetalleDTO {
    private Taller taller;
    private InstructorDTO instructor;
    private AlumnoDTO alumno;
}
