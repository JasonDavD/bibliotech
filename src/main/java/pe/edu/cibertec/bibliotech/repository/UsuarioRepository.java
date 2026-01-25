package pe.edu.cibertec.bibliotech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.bibliotech.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByDni(String dni);
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByActivoTrue();
    
    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% OR u.apellido LIKE %:nombre%")
    List<Usuario> buscarPorNombre(@Param("nombre") String nombre);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    Long contarUsuariosActivos();
}