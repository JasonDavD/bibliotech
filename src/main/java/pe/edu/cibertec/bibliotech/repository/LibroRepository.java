package pe.edu.cibertec.bibliotech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.bibliotech.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    // Métodos derivados de Spring Data JPA
    Optional<Libro> findByIsbn(String isbn);
    
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    
    List<Libro> findByCategoriaId(Long categoriaId);
    
    List<Libro> findByAutorId(Long autorId);
    
    // Consultas JPQL personalizadas
    @Query("SELECT l FROM Libro l WHERE l.cantidadDisponible > 0")
    List<Libro> findLibrosDisponibles();
    
    @Query("SELECT l FROM Libro l WHERE l.categoria.nombre = :categoria")
    List<Libro> findByCategoriaNombre(@Param("categoria") String categoria);
    
    @Query("SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.autor.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Libro> buscarPorTituloOAutor(@Param("keyword") String keyword);
    
    @Query("SELECT l FROM Libro l LEFT JOIN l.prestamos p GROUP BY l ORDER BY COUNT(p) DESC")
    List<Libro> findLibrosMasPrestados();
    
    @Query("SELECT COUNT(l) FROM Libro l WHERE l.cantidadDisponible > 0")
    Long contarLibrosDisponibles();
    
    @Query("SELECT SUM(l.cantidadTotal) FROM Libro l")
    Long contarTotalEjemplares();
    
    @Query("SELECT l FROM Libro l WHERE l.cantidadDisponible = 0")
    List<Libro> findLibrosSinStock();
    
    @Query("SELECT l FROM Libro l WHERE l.cantidadDisponible > 0 AND l.cantidadDisponible <= 2")
    List<Libro> findLibrosStockBajo();
}