package pe.edu.upeu.ms_gestion_alumno.controller;

import jakarta.validation.Valid;
import pe.edu.upeu.ms_gestion_alumno.dto.AlumnoDTO;
import pe.edu.upeu.ms_gestion_alumno.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    private final AlumnoService service;

    public AlumnoController(AlumnoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AlumnoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlumnoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlumnoDTO> crear(@Valid @RequestBody AlumnoDTO dto) {
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlumnoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/incrementar-taller")
    public ResponseEntity<AlumnoDTO> incrementarTaller(@PathVariable Long id) {
        return ResponseEntity.ok(service.incrementarTaller(id));
    }

    @PostMapping("/{id}/decrementar-taller")
    public ResponseEntity<AlumnoDTO> decrementarTaller(@PathVariable Long id) {
        return ResponseEntity.ok(service.decrementarTaller(id));
    }
}
