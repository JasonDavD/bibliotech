package pe.edu.cibertec.bibliotech.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    
    @Column(length = 200)
    private String descripcion;
    
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @OneToMany(mappedBy = "categoria")
    private List<Libro> libros = new ArrayList<>();
}