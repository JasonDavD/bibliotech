package pe.edu.cibertec.bibliotech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.bibliotech.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    Optional<Categoria> findByNombre(String nombre);
}