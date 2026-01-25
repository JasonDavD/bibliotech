package pe.edu.cibertec.bibliotech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.bibliotech.entity.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    
    List<Autor> findByNombreContainingIgnoreCase(String nombre);
}