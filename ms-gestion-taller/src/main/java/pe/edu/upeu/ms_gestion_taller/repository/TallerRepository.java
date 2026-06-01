package pe.edu.upeu.ms_gestion_taller.repository;

import pe.edu.upeu.ms_gestion_taller.entity.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long> {
}
