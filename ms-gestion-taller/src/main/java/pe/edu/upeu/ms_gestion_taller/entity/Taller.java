package pe.edu.upeu.ms_gestion_taller.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "talleres")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private Long instructorId;

    @Column(nullable = false)
    private Integer cupoMaximo = 10;

    @Column(nullable = false)
    private Integer inscritos = 0;

    @Column(nullable = false)
    private Boolean estado = true;
}
