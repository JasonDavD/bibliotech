package pe.edu.cibertec.bibliotech.service;

import java.util.List;

import pe.edu.cibertec.bibliotech.api.request.LibroRequestDto;
import pe.edu.cibertec.bibliotech.api.response.LibroResponseDto;

public interface LibroService {
    
    LibroResponseDto crear(LibroRequestDto request);
    
    LibroResponseDto actualizar(Long id, LibroRequestDto request);
    
    void eliminar(Long id);
    
    LibroResponseDto obtener(Long id);
    
    List<LibroResponseDto> listar();
    
    List<LibroResponseDto> listarDisponibles();
    
    List<LibroResponseDto> buscarPorTitulo(String titulo);
    
    List<LibroResponseDto> buscarPorAutor(Long autorId);
    
    List<LibroResponseDto> buscarPorCategoria(Long categoriaId);
    
    List<LibroResponseDto> buscarPorPalabra(String keyword);
}