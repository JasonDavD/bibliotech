package pe.edu.cibertec.bibliotech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    @Column(nullable = false, unique = true, length = 8)
    private String dni;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Column(length = 15)
    private String telefono;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Prestamo> prestamos = new ArrayList<>();
    
    // Método de utilidad
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}