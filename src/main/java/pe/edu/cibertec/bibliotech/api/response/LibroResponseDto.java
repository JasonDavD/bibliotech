package pe.edu.cibertec.bibliotech.api.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LibroResponseDto {
    
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anioPublicacion;
    private Integer cantidadTotal;
    private Integer cantidadDisponible;
    private String descripcion;
    private String autorNombre;
    private String categoriaNombre;
    private LocalDateTime fechaRegistro;
    private boolean disponible;
}