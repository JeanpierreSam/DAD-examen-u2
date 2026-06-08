package pe.edu.upeu.ms_gestion_taller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TallerDTO {
    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private Long instructorId;

    @NotNull(message = "El cupo maximo es obligatorio")
    @Min(value = 1, message = "El cupo maximo debe ser al menos 1")
    private Integer cupoMaximo;

    private Boolean estado;
    private Integer inscritos;
}
