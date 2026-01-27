package pe.edu.cibertec.bibliotech.api.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CategoriaResponseDto {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    private Integer cantidadLibros;
}