package pe.edu.cibertec.bibliotech.service;

import java.util.List;

import pe.edu.cibertec.bibliotech.api.request.AutorRequestDto;
import pe.edu.cibertec.bibliotech.api.response.AutorResponseDto;

public interface AutorService {
    
    AutorResponseDto crear(AutorRequestDto request);
    
    AutorResponseDto actualizar(Long id, AutorRequestDto request);
    
    void eliminar(Long id);
    
    AutorResponseDto obtener(Long id);
    
    List<AutorResponseDto> listar();
    
    List<AutorResponseDto> buscarPorNombre(String nombre);
}