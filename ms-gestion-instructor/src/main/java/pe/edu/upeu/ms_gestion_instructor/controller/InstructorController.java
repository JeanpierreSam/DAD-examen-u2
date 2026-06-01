package pe.edu.upeu.ms_gestion_instructor.controller;

import pe.edu.upeu.ms_gestion_instructor.dto.InstructorDTO;
import pe.edu.upeu.ms_gestion_instructor.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructores")
public class InstructorController {

    @Autowired
    private InstructorService service;

    @PostMapping
    public ResponseEntity<InstructorDTO> crear(@RequestBody InstructorDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<InstructorDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
