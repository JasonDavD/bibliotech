package pe.edu.cibertec.bibliotech.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AutorRequestDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 100, message = "La nacionalidad no puede exceder 100 caracteres")
    private String nacionalidad;
}