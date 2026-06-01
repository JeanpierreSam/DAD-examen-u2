package pe.edu.upeu.ms_gestion_alumno.service;

import pe.edu.upeu.ms_gestion_alumno.dto.AlumnoDTO;
import pe.edu.upeu.ms_gestion_alumno.entity.Alumno;
import pe.edu.upeu.ms_gestion_alumno.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    @Autowired
    private AlumnoRepository repository;

    @Override
    public AlumnoDTO crear(AlumnoDTO dto) {
        Alumno alumno = Alumno.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .grado(dto.getGrado())
                .build();
        alumno = repository.save(alumno);
        return mapToDTO(alumno);
    }

    @Override
    public AlumnoDTO obtenerPorId(Long id) {
        Alumno alumno = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        return mapToDTO(alumno);
    }

    @Override
    public List<AlumnoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AlumnoDTO mapToDTO(Alumno alumno) {
        return AlumnoDTO.builder()
                .id(alumno.getId())
                .nombre(alumno.getNombre())
                .email(alumno.getEmail())
                .grado(alumno.getGrado())
                .build();
    }
}
