package pe.edu.upeu.ms_gestion_taller.controller;

import jakarta.validation.Valid;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDetalleDTO;
import pe.edu.upeu.ms_gestion_taller.service.TallerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talleres")
public class TallerController {

    private final TallerService service;

    public TallerController(TallerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TallerDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TallerDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TallerDTO> crear(@Valid @RequestBody TallerDTO dto) {
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TallerDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TallerDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tallerId}/asignar-instructor/{instructorId}")
    public ResponseEntity<TallerDTO> asignarInstructor(@PathVariable Long tallerId, @PathVariable Long instructorId) {
        return ResponseEntity.ok(service.asignarInstructor(tallerId, instructorId));
    }

    @PostMapping("/{tallerId}/inscribir-alumno/{alumnoId}")
    public ResponseEntity<TallerDTO> inscribirAlumno(@PathVariable Long tallerId, @PathVariable Long alumnoId) {
        return new ResponseEntity<>(service.inscribirAlumno(tallerId, alumnoId), HttpStatus.CREATED);
    }

    @PostMapping("/{tallerId}/matricular-alumno/{alumnoId}")
    public ResponseEntity<TallerDTO> matricularAlumno(@PathVariable Long tallerId, @PathVariable Long alumnoId) {
        return new ResponseEntity<>(service.matricularAlumno(tallerId, alumnoId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/detalle-completo")
    public ResponseEntity<TallerDetalleDTO> obtenerDetalleCompleto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerDetalleCompleto(id));
    }
}
