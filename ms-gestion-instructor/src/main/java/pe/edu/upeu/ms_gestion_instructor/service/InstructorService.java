package pe.edu.upeu.ms_gestion_instructor.service;

import pe.edu.upeu.ms_gestion_instructor.dto.InstructorDTO;
import java.util.List;

public interface InstructorService {
    List<InstructorDTO> listarTodos();
    InstructorDTO obtenerPorId(Long id);
    InstructorDTO crear(InstructorDTO dto);
    InstructorDTO actualizar(Long id, InstructorDTO dto);
    void eliminar(Long id);
}
