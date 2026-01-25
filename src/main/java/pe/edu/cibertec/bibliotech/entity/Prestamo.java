package pe.edu.cibertec.bibliotech.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    @NotNull
    private Libro libro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;
    
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo = LocalDate.now();
    
    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDate fechaDevolucionEsperada;
    
    @Column(name = "fecha_devolucion_real")
    private LocalDate fechaDevolucionReal;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;
    
    @Column(length = 500)
    private String observaciones;
    
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    // Enum para estados
    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, VENCIDO
    }
    
    // Constructor personalizado
    public Prestamo(Libro libro, Usuario usuario) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucionEsperada = LocalDate.now().plusDays(14);
    }
    
    // Métodos de utilidad
    public boolean isVencido() {
        return estado == EstadoPrestamo.ACTIVO && 
               LocalDate.now().isAfter(fechaDevolucionEsperada);
    }
    
    public long getDiasAtraso() {
        if (isVencido()) {
            return ChronoUnit.DAYS.between(fechaDevolucionEsperada, LocalDate.now());
        }
        return 0;
    }
}