package pe.edu.cibertec.bibliotech.api.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private Integer prestamosActivos;
}