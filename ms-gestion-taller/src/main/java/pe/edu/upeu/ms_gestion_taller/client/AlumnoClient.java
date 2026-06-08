package pe.edu.upeu.ms_gestion_taller.client;

import pe.edu.upeu.ms_gestion_taller.dto.AlumnoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ms-gestion-alumno", path = "/api/alumnos")
public interface AlumnoClient {
    @GetMapping("/{id}")
    AlumnoDTO obtenerPorId(@PathVariable("id") Long id);

    @PostMapping("/{id}/incrementar-taller")
    AlumnoDTO incrementarTaller(@PathVariable("id") Long id);

    @PostMapping("/{id}/decrementar-taller")
    AlumnoDTO decrementarTaller(@PathVariable("id") Long id);
}
