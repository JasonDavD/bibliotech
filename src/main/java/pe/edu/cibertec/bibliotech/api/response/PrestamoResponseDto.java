package pe.edu.cibertec.bibliotech.api.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PrestamoResponseDto {
    
    private Long id;
    private Long libroId;
    private String libroTitulo;
    private Long usuarioId;
    private String usuarioNombreCompleto;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private String estado;
    private String observaciones;
    private boolean vencido;
    private Long diasAtraso;
}