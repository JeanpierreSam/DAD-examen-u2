package pe.edu.upeu.ms_gestion_alumno.service;

import pe.edu.upeu.ms_gestion_alumno.dto.AlumnoDTO;
import java.util.List;

public interface AlumnoService {
    List<AlumnoDTO> listarTodos();
    AlumnoDTO obtenerPorId(Long id);
    AlumnoDTO crear(AlumnoDTO dto);
    AlumnoDTO actualizar(Long id, AlumnoDTO dto);
    void eliminar(Long id);
    AlumnoDTO incrementarTaller(Long id);
    AlumnoDTO decrementarTaller(Long id);
}
