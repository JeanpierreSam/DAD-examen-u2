package pe.edu.upeu.ms_gestion_taller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TallerDetalleDTO {
    private TallerDTO taller;
    private InstructorDTO instructor;
    private List<AlumnoDTO> alumnosInscritos;
}
