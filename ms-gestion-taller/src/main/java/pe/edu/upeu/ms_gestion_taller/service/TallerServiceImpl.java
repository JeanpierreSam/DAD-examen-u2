package pe.edu.upeu.ms_gestion_taller.service;

import pe.edu.upeu.ms_gestion_taller.client.AlumnoClient;
import pe.edu.upeu.ms_gestion_taller.client.InstructorClient;
import pe.edu.upeu.ms_gestion_taller.dto.AlumnoDTO;
import pe.edu.upeu.ms_gestion_taller.dto.InstructorDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDetalleDTO;
import pe.edu.upeu.ms_gestion_taller.entity.Taller;
import pe.edu.upeu.ms_gestion_taller.exception.BusinessException;
import pe.edu.upeu.ms_gestion_taller.exception.ResourceNotFoundException;
import pe.edu.upeu.ms_gestion_taller.repository.TallerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TallerServiceImpl implements TallerService {

    private final TallerRepository repository;
    private final InstructorClient instructorClient;
    private final AlumnoClient alumnoClient;

    public TallerServiceImpl(TallerRepository repository,
                             InstructorClient instructorClient,
                             AlumnoClient alumnoClient) {
        this.repository = repository;
        this.instructorClient = instructorClient;
        this.alumnoClient = alumnoClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TallerDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TallerDTO obtenerPorId(Long id) {
        Taller taller = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + id));
        return mapToDTO(taller);
    }

    @Override
    @Transactional
    public TallerDTO crear(TallerDTO dto) {
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un taller con el codigo " + dto.getCodigo());
        }
        Taller taller = Taller.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .instructorId(dto.getInstructorId())
                .cupoMaximo(dto.getCupoMaximo() != null ? dto.getCupoMaximo() : 10)
                .inscritos(0)
                .estado(true)
                .build();
        return mapToDTO(repository.save(taller));
    }

    @Override
    @Transactional
    public TallerDTO actualizar(Long id, TallerDTO dto) {
        Taller taller = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + id));
        taller.setNombre(dto.getNombre());
        taller.setDescripcion(dto.getDescripcion());
        taller.setInstructorId(dto.getInstructorId());
        if (dto.getCupoMaximo() != null) {
            taller.setCupoMaximo(dto.getCupoMaximo());
        }
        if (dto.getEstado() != null) {
            taller.setEstado(dto.getEstado());
        }
        return mapToDTO(repository.save(taller));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Taller no encontrado con id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "tallerService", fallbackMethod = "fallbackAsignarInstructor")
    public TallerDTO asignarInstructor(Long tallerId, Long instructorId) {
        Taller taller = repository.findById(tallerId)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + tallerId));
        instructorClient.obtenerPorId(instructorId);
        taller.setInstructorId(instructorId);
        return mapToDTO(repository.save(taller));
    }

    public TallerDTO fallbackAsignarInstructor(Long tallerId, Long instructorId, Throwable t) {
        throw new RuntimeException("Servicio de instructor no disponible. Operacion revertida.");
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "tallerService", fallbackMethod = "fallbackInscribirAlumno")
    public TallerDTO inscribirAlumno(Long tallerId, Long alumnoId) {
        Taller taller = repository.findById(tallerId)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + tallerId));
        if (taller.getInscritos() >= taller.getCupoMaximo()) {
            throw new BusinessException("Taller lleno. Cupo maximo alcanzado.");
        }
        alumnoClient.obtenerPorId(alumnoId);
        taller.setInscritos(taller.getInscritos() + 1);
        repository.save(taller);
        alumnoClient.incrementarTaller(alumnoId);
        return mapToDTO(taller);
    }

    public TallerDTO fallbackInscribirAlumno(Long tallerId, Long alumnoId, Throwable t) {
        throw new RuntimeException("Servicio de alumno no disponible. Operacion revertida.");
    }

    @Override
    @Transactional
    public TallerDTO matricularAlumno(Long tallerId, Long alumnoId) {
        Taller taller = repository.findById(tallerId)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + tallerId));
        if (taller.getInscritos() >= taller.getCupoMaximo()) {
            try {
                alumnoClient.obtenerPorId(alumnoId);
            } catch (Exception e) {
                throw new BusinessException("Alumno no encontrado. Operacion revertida.");
            }
            throw new BusinessException("Taller lleno. Cupo maximo alcanzado. Operacion revertida.");
        }
        alumnoClient.obtenerPorId(alumnoId);
        taller.setInscritos(taller.getInscritos() + 1);
        repository.save(taller);
        try {
            alumnoClient.incrementarTaller(alumnoId);
        } catch (Exception e) {
            taller.setInscritos(taller.getInscritos() - 1);
            repository.save(taller);
            throw new BusinessException("Error al actualizar alumno. Operacion revertida.");
        }
        return mapToDTO(taller);
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "tallerService", fallbackMethod = "fallbackDetalle")
    public TallerDetalleDTO obtenerDetalleCompleto(Long id) {
        Taller taller = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + id));
        InstructorDTO instructor = null;
        if (taller.getInstructorId() != null) {
            try {
                instructor = instructorClient.obtenerPorId(taller.getInstructorId());
            } catch (Exception e) {
                instructor = InstructorDTO.builder().nombres("No disponible").build();
            }
        }
        List<AlumnoDTO> alumnos = Collections.emptyList();
        return TallerDetalleDTO.builder()
                .taller(mapToDTO(taller))
                .instructor(instructor)
                .alumnosInscritos(alumnos)
                .build();
    }

    public TallerDetalleDTO fallbackDetalle(Long id, Throwable t) {
        Taller taller = repository.findById(id).orElse(null);
        return TallerDetalleDTO.builder()
                .taller(taller != null ? mapToDTO(taller) : null)
                .instructor(InstructorDTO.builder().nombres("No disponible").build())
                .alumnosInscritos(Collections.emptyList())
                .build();
    }

    private TallerDTO mapToDTO(Taller taller) {
        return TallerDTO.builder()
                .id(taller.getId())
                .codigo(taller.getCodigo())
                .nombre(taller.getNombre())
                .descripcion(taller.getDescripcion())
                .instructorId(taller.getInstructorId())
                .cupoMaximo(taller.getCupoMaximo())
                .inscritos(taller.getInscritos())
                .estado(taller.getEstado())
                .build();
    }
}
