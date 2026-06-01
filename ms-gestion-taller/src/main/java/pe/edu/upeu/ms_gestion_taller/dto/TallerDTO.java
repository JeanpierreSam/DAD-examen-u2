package pe.edu.upeu.ms_gestion_taller.dto;

import lombok.*;

@Data
@Builder
public class TallerDTO {
    private Long id;
    private String nombre;
    private Long instructorId;
}
