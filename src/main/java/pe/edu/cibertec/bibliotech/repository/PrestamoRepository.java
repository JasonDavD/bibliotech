package pe.edu.cibertec.bibliotech.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.bibliotech.entity.Prestamo;
import pe.edu.cibertec.bibliotech.entity.Prestamo.EstadoPrestamo;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    
    // Métodos derivados
    List<Prestamo> findByUsuarioId(Long usuarioId);
    
    List<Prestamo> findByLibroId(Long libroId);
    
    List<Prestamo> findByEstado(EstadoPrestamo estado);
    
    // Consultas JPQL personalizadas
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEsperada < :fecha")
    List<Prestamo> findPrestamosVencidos(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.estado = 'ACTIVO'")
    Long contarPrestamosActivos();
    
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVO'")
    List<Prestamo> findPrestamosActivosPorUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Prestamo p WHERE p.libro.id = :libroId AND p.estado = 'ACTIVO'")
    List<Prestamo> findPrestamosActivosPorLibro(@Param("libroId") Long libroId);
    
    @Query("SELECT p FROM Prestamo p WHERE p.fechaPrestamo BETWEEN :inicio AND :fin")
    List<Prestamo> findByFechaRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'DEVUELTO' AND p.fechaDevolucionReal > p.fechaDevolucionEsperada")
    List<Prestamo> findDevueltosConAtraso();
    
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.estado = :estado")
    Long contarPorEstado(@Param("estado") EstadoPrestamo estado);
}