package pe.edu.cibertec.bibliotech.service;

import java.util.List;

import pe.edu.cibertec.bibliotech.api.request.CategoriaRequestDto;
import pe.edu.cibertec.bibliotech.api.response.CategoriaResponseDto;

public interface CategoriaService {
    
    CategoriaResponseDto crear(CategoriaRequestDto request);
    
    CategoriaResponseDto actualizar(Long id, CategoriaRequestDto request);
    
    void eliminar(Long id);
    
    CategoriaResponseDto obtener(Long id);
    
    List<CategoriaResponseDto> listar();
    
    CategoriaResponseDto buscarPorNombre(String nombre);
}