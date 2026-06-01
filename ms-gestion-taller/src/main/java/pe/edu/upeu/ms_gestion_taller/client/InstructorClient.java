package pe.edu.upeu.ms_gestion_taller.client;

import pe.edu.upeu.ms_gestion_taller.dto.InstructorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-gestion-instructor", path = "/api/instructores")
public interface InstructorClient {
    @GetMapping("/{id}")
    InstructorDTO obtenerPorId(@PathVariable("id") Long id);
}
