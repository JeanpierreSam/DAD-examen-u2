package pe.edu.upeu.ms_gestion_instructor.service;

import pe.edu.upeu.ms_gestion_instructor.dto.InstructorDTO;
import pe.edu.upeu.ms_gestion_instructor.entity.Instructor;
import pe.edu.upeu.ms_gestion_instructor.exception.BusinessException;
import pe.edu.upeu.ms_gestion_instructor.exception.ResourceNotFoundException;
import pe.edu.upeu.ms_gestion_instructor.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository repository;

    public InstructorServiceImpl(InstructorRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorDTO obtenerPorId(Long id) {
        Instructor instructor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor no encontrado con id " + id));
        return mapToDTO(instructor);
    }

    @Override
    @Transactional
    public InstructorDTO crear(InstructorDTO dto) {
        if (repository.existsByDni(dto.getDni())) {
            throw new BusinessException("Ya existe un instructor con el DNI " + dto.getDni());
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Ya existe un instructor con el email " + dto.getEmail());
        }
        Instructor instructor = Instructor.builder()
                .dni(dto.getDni())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .especialidad(dto.getEspecialidad())
                .email(dto.getEmail())
                .estado(true)
                .build();
        return mapToDTO(repository.save(instructor));
    }

    @Override
    @Transactional
    public InstructorDTO actualizar(Long id, InstructorDTO dto) {
        Instructor instructor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor no encontrado con id " + id));
        instructor.setNombres(dto.getNombres());
        instructor.setApellidos(dto.getApellidos());
        instructor.setEspecialidad(dto.getEspecialidad());
        instructor.setEmail(dto.getEmail());
        if (dto.getEstado() != null) {
            instructor.setEstado(dto.getEstado());
        }
        return mapToDTO(repository.save(instructor));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor no encontrado con id " + id);
        }
        repository.deleteById(id);
    }

    private InstructorDTO mapToDTO(Instructor instructor) {
        return InstructorDTO.builder()
                .id(instructor.getId())
                .dni(instructor.getDni())
                .nombres(instructor.getNombres())
                .apellidos(instructor.getApellidos())
                .especialidad(instructor.getEspecialidad())
                .email(instructor.getEmail())
                .estado(instructor.getEstado())
                .build();
    }
}
