package pe.edu.cibertec.bibliotech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 200)
    private String titulo;
    
    @NotBlank(message = "El ISBN es obligatorio")
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;
    
    @Min(value = 1900, message = "El año debe ser válido")
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;
    
    @Min(value = 1, message = "Debe haber al menos 1 ejemplar")
    @Column(name = "cantidad_total", nullable = false)
    private Integer cantidadTotal = 1;
    
    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible = 1;
    
    @Column(length = 1000)
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    private List<Prestamo> prestamos = new ArrayList<>();
    
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();
    
    // Método de utilidad
    public boolean isDisponible() {
        return cantidadDisponible != null && cantidadDisponible > 0;
    }
}