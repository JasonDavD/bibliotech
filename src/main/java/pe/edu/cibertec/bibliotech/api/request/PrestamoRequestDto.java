package pe.edu.cibertec.bibliotech.api.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PrestamoRequestDto {
    
    @NotNull(message = "El libro es obligatorio")
    private Long libroId;
    
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;
    
    @Future(message = "La fecha de devolución debe ser futura")
    private LocalDate fechaDevolucionEsperada;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}