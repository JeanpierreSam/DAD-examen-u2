package pe.edu.upeu.ms_gestion_taller.controller;

import pe.edu.upeu.ms_gestion_taller.dto.TallerDTO;
import pe.edu.upeu.ms_gestion_taller.dto.TallerDetalleDTO;
import pe.edu.upeu.ms_gestion_taller.service.TallerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talleres")
public class TallerController {

    @Autowired
    private TallerService service;

    @PostMapping
    public ResponseEntity<TallerDTO> crear(@RequestBody TallerDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TallerDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<TallerDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerDetalle(id));
    }

    @GetMapping
    public ResponseEntity<List<TallerDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
