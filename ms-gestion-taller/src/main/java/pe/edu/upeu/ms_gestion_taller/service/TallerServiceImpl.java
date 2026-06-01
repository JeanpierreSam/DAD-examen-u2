package pe.edu.upeu.ms_gestion_taller.service;

import pe.edu.upeu.ms_gestion_taller.client.AlumnoClient;
import pe.edu.upeu.ms_gestion_taller.client.InstructorClient;
import pe.edu.upeu.ms_gestion_taller.dto.AlumnoDTO;
import pe.edu.upeu.ms_gestion_taller.dto.InstructorDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDetalleDTO;
import pe.edu.upeu.ms_gestion_taller.entity.Taller;
import pe.edu.upeu.ms_gestion_taller.repository.TallerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TallerServiceImpl implements TallerService {

    @Autowired
    private TallerRepository repository;
    @Autowired
    private InstructorClient instructorClient;
    @Autowired
    private AlumnoClient alumnoClient;

    @Override
    public TallerDTO crear(TallerDTO dto) {
        Taller taller = Taller.builder()
                .nombre(dto.getNombre())
                .instructorId(dto.getInstructorId())
                .build();
        taller = repository.save(taller);
        return mapToDTO(taller);
    }

    @Override
    public TallerDTO obtenerPorId(Long id) {
        Taller taller = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Taller no encontrado"));
        return mapToDTO(taller);
    }

    @Override
    public List<TallerDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CircuitBreaker(name = "tallerService", fallbackMethod = "fallbackDetalle")
    public TallerDetalleDTO obtenerDetalle(Long id) {
        Taller taller = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Taller no encontrado"));
        
        InstructorDTO instructor = instructorClient.obtenerPorId(taller.getInstructorId());
        // For simplicity, assume Taller has alumnoId, but diagram said "gestión taller". 
        // Let's assume a simplified relationship or just fetch 1 alumno for demo.
        AlumnoDTO alumno = alumnoClient.obtenerPorId(1L); // Placeholder

        return TallerDetalleDTO.builder()
                .taller(taller)
                .instructor(instructor)
                .alumno(alumno)
                .build();
    }

    public TallerDetalleDTO fallbackDetalle(Long id, Throwable t) {
        Taller taller = repository.findById(id).orElse(null);
        return TallerDetalleDTO.builder()
                .taller(taller)
                .instructor(InstructorDTO.builder().nombre("No disponible").build())
                .alumno(AlumnoDTO.builder().nombre("No disponible").build())
                .build();
    }

    private TallerDTO mapToDTO(Taller taller) {
        return TallerDTO.builder()
                .id(taller.getId())
                .nombre(taller.getNombre())
                .instructorId(taller.getInstructorId())
                .build();
    }
}
