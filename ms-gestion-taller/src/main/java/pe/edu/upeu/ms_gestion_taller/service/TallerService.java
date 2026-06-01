package pe.edu.upeu.ms_gestion_taller.service;

import pe.edu.upeu.ms_gestion_taller.dto.TallerDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDetalleDTO;
import java.util.List;

public interface TallerService {
    TallerDTO crear(TallerDTO tallerDTO);
    TallerDTO obtenerPorId(Long id);
    List<TallerDTO> listarTodos();
    TallerDetalleDTO obtenerDetalle(Long id);
}
