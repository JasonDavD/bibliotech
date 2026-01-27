package pe.edu.cibertec.bibliotech.api.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AutorResponseDto {
    
    private Long id;
    private String nombre;
    private String nacionalidad;
    private LocalDateTime fechaRegistro;
    private Integer cantidadLibros;
}