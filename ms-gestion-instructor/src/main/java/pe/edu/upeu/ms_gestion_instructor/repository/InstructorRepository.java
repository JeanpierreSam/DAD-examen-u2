package pe.edu.upeu.ms_gestion_instructor.repository;

import pe.edu.upeu.ms_gestion_instructor.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
