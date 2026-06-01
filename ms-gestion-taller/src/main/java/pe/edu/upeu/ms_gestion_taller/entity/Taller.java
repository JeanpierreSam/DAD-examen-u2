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
    
    @Column(nullable = false)
    private String nombre;
    
    private Long instructorId;
}
