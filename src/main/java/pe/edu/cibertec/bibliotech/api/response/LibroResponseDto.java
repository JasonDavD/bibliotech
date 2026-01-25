package pe.edu.cibertec.bibliotech.api.response;

import lombok.Data;
import java.time.LocalDateTime;

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