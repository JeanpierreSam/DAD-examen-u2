package pe.edu.upeu.ms_gestion_alumno.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alumnos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer ciclo;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer talleresInscritos = 0;
}
