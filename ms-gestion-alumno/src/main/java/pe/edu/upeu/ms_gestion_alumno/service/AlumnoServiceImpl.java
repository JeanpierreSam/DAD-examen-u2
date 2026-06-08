package pe.edu.upeu.ms_gestion_alumno.service;

import pe.edu.upeu.ms_gestion_alumno.dto.AlumnoDTO;
import pe.edu.upeu.ms_gestion_alumno.entity.Alumno;
import pe.edu.upeu.ms_gestion_alumno.exception.BusinessException;
import pe.edu.upeu.ms_gestion_alumno.exception.ResourceNotFoundException;
import pe.edu.upeu.ms_gestion_alumno.repository.AlumnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository repository;

    public AlumnoServiceImpl(AlumnoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoDTO obtenerPorId(Long id) {
        Alumno alumno = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id " + id));
        return mapToDTO(alumno);
    }

    @Override
    @Transactional
    public AlumnoDTO crear(AlumnoDTO dto) {
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un alumno con el codigo " + dto.getCodigo());
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un alumno con el email " + dto.getEmail());
        }
        Alumno alumno = Alumno.builder()
                .codigo(dto.getCodigo())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .ciclo(dto.getCiclo())
                .estado(true)
                .talleresInscritos(0)
                .build();
        return mapToDTO(repository.save(alumno));
    }

    @Override
    @Transactional
    public AlumnoDTO actualizar(Long id, AlumnoDTO dto) {
        Alumno alumno = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id " + id));
        alumno.setNombres(dto.getNombres());
        alumno.setApellidos(dto.getApellidos());
        alumno.setEmail(dto.getEmail());
        alumno.setCiclo(dto.getCiclo());
        if (dto.getEstado() != null) {
            alumno.setEstado(dto.getEstado());
        }
        return mapToDTO(repository.save(alumno));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Alumno no encontrado con id " + id);
        }
        repository.deleteById(id);
    }

    private static final int MAX_TALLERES = 3;

    @Override
    @Transactional
    public AlumnoDTO incrementarTaller(Long id) {
        Alumno alumno = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id " + id));
        int actual = alumno.getTalleresInscritos() == null ? 0 : alumno.getTalleresInscritos();
        if (actual >= MAX_TALLERES) {
            throw new BusinessException("El alumno " + id + " ya alcanzo el maximo de " + MAX_TALLERES + " talleres");
        }
        alumno.setTalleresInscritos(actual + 1);
        return mapToDTO(repository.save(alumno));
    }

    @Override
    @Transactional
    public AlumnoDTO decrementarTaller(Long id) {
        Alumno alumno = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id " + id));
        int actual = alumno.getTalleresInscritos() == null ? 0 : alumno.getTalleresInscritos();
        alumno.setTalleresInscritos(Math.max(0, actual - 1));
        return mapToDTO(repository.save(alumno));
    }

    private AlumnoDTO mapToDTO(Alumno alumno) {
        return AlumnoDTO.builder()
                .id(alumno.getId())
                .codigo(alumno.getCodigo())
                .nombres(alumno.getNombres())
                .apellidos(alumno.getApellidos())
                .email(alumno.getEmail())
                .ciclo(alumno.getCiclo())
                .estado(alumno.getEstado())
                .talleresInscritos(alumno.getTalleresInscritos())
                .build();
    }
}
