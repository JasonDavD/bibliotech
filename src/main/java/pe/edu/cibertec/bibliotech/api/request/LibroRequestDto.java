package pe.edu.cibertec.bibliotech.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LibroRequestDto {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;
    
    @NotBlank(message = "El ISBN es obligatorio")
    @Size(max = 20, message = "El ISBN no puede exceder 20 caracteres")
    private String isbn;
    
    @Min(value = 1900, message = "El año debe ser mayor a 1900")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    private Integer anioPublicacion;
    
    @Min(value = 1, message = "Debe haber al menos 1 ejemplar")
    private Integer cantidadTotal;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotNull(message = "El autor es obligatorio")
    private Long autorId;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
}