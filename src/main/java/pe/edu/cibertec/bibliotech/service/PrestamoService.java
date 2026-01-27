package pe.edu.cibertec.bibliotech.service;

import java.util.List;

import pe.edu.cibertec.bibliotech.api.request.PrestamoRequestDto;
import pe.edu.cibertec.bibliotech.api.response.PrestamoResponseDto;

public interface PrestamoService {
    
    PrestamoResponseDto registrarPrestamo(PrestamoRequestDto request);
    
    PrestamoResponseDto registrarDevolucion(Long prestamoId, String observaciones);
    
    void cancelar(Long id);
    
    PrestamoResponseDto obtener(Long id);
    
    List<PrestamoResponseDto> listar();
    
    List<PrestamoResponseDto> listarActivos();
    
    List<PrestamoResponseDto> listarVencidos();
    
    List<PrestamoResponseDto> listarPorUsuario(Long usuarioId);
    
    List<PrestamoResponseDto> listarPorLibro(Long libroId);
    
    void actualizarEstadosVencidos();
}