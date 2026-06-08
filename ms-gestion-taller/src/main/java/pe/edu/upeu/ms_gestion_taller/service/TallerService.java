package pe.edu.upeu.ms_gestion_taller.service;

import pe.edu.upeu.ms_gestion_taller.dto.TallerDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDetalleDTO;
import java.util.List;

public interface TallerService {
    List<TallerDTO> listarTodos();
    TallerDTO obtenerPorId(Long id);
    TallerDTO crear(TallerDTO dto);
    TallerDTO actualizar(Long id, TallerDTO dto);
    void eliminar(Long id);
    TallerDTO asignarInstructor(Long tallerId, Long instructorId);
    TallerDTO inscribirAlumno(Long tallerId, Long alumnoId);
    TallerDTO matricularAlumno(Long tallerId, Long alumnoId);
    TallerDetalleDTO obtenerDetalleCompleto(Long id);
}
