package pe.edu.upeu.ms_gestion_alumno.service;

import pe.edu.upeu.ms_gestion_alumno.dto.AlumnoDTO;
import java.util.List;

public interface AlumnoService {
    AlumnoDTO crear(AlumnoDTO alumnoDTO);
    AlumnoDTO obtenerPorId(Long id);
    List<AlumnoDTO> listarTodos();
}
