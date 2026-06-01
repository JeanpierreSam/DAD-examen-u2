package pe.edu.upeu.ms_gestion_instructor.service;

import pe.edu.upeu.ms_gestion_instructor.dto.InstructorDTO;
import java.util.List;

public interface InstructorService {
    InstructorDTO crear(InstructorDTO instructorDTO);
    InstructorDTO obtenerPorId(Long id);
    List<InstructorDTO> listarTodos();
}
