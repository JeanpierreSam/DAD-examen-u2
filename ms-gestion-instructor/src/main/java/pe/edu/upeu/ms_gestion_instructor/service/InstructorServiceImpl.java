package pe.edu.upeu.ms_gestion_instructor.service;

import pe.edu.upeu.ms_gestion_instructor.dto.InstructorDTO;
import pe.edu.upeu.ms_gestion_instructor.entity.Instructor;
import pe.edu.upeu.ms_gestion_instructor.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService {

    @Autowired
    private InstructorRepository repository;

    @Override
    public InstructorDTO crear(InstructorDTO dto) {
        Instructor instructor = Instructor.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .especialidad(dto.getEspecialidad())
                .build();
        instructor = repository.save(instructor);
        return mapToDTO(instructor);
    }

    @Override
    public InstructorDTO obtenerPorId(Long id) {
        Instructor instructor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));
        return mapToDTO(instructor);
    }

    @Override
    public List<InstructorDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private InstructorDTO mapToDTO(Instructor instructor) {
        return InstructorDTO.builder()
                .id(instructor.getId())
                .nombre(instructor.getNombre())
                .email(instructor.getEmail())
                .especialidad(instructor.getEspecialidad())
                .build();
    }
}
