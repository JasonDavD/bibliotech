package pe.edu.cibertec.bibliotech.service;

import java.util.List;

import pe.edu.cibertec.bibliotech.api.request.UsuarioRequestDto;
import pe.edu.cibertec.bibliotech.api.response.UsuarioResponseDto;

public interface UsuarioService {
    
    UsuarioResponseDto crear(UsuarioRequestDto request);
    
    UsuarioResponseDto actualizar(Long id, UsuarioRequestDto request);
    
    void eliminar(Long id);
    
    UsuarioResponseDto obtener(Long id);
    
    List<UsuarioResponseDto> listar();
    
    List<UsuarioResponseDto> listarActivos();
    
    UsuarioResponseDto buscarPorDni(String dni);
    
    UsuarioResponseDto buscarPorEmail(String email);
    
    void activar(Long id);
    
    void desactivar(Long id);
}