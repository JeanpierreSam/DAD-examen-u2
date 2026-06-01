package pe.edu.upeu.ms_gestion_alumno.repository;

import pe.edu.upeu.ms_gestion_alumno.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
}
