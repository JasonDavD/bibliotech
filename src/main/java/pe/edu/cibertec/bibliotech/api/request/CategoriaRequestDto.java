package pe.edu.cibertec.bibliotech.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequestDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;
}