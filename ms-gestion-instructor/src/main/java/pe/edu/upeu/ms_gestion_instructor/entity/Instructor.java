package pe.edu.upeu.ms_gestion_instructor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instructores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean estado = true;
}
