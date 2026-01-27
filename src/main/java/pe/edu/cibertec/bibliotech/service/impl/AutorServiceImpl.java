package pe.edu.cibertec.bibliotech.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.cibertec.bibliotech.api.request.AutorRequestDto;
import pe.edu.cibertec.bibliotech.api.response.AutorResponseDto;
import pe.edu.cibertec.bibliotech.entity.Autor;
import pe.edu.cibertec.bibliotech.exception.BusinessException;
import pe.edu.cibertec.bibliotech.exception.NotFoundException;
import pe.edu.cibertec.bibliotech.mapper.AutorMapper;
import pe.edu.cibertec.bibliotech.repository.AutorRepository;
import pe.edu.cibertec.bibliotech.service.AutorService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AutorServiceImpl implements AutorService {
    
    private final AutorRepository autorRepo;
    private final AutorMapper mapper;
    
    @Override
    public AutorResponseDto crear(AutorRequestDto req) {
        log.info("Creando autor: nombre='{}'", req.getNombre());
        
        Autor entity = mapper.toEntity(req);
        entity.setNombre(req.getNombre().trim());
        
        if (req.getNacionalidad() != null && !req.getNacionalidad().trim().isEmpty()) {
            entity.setNacionalidad(req.getNacionalidad().trim());
        }
        
        entity.setFechaRegistro(LocalDateTime.now());
        
        Autor saved = autorRepo.save(entity);
        log.info("✓ Autor creado id={}, nombre='{}'", saved.getId(), saved.getNombre());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public AutorResponseDto actualizar(Long id, AutorRequestDto req) {
        log.info("Actualizando autor id={}", id);
        
        Autor actual = autorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor no encontrado: " + id));
        
        mapper.updateEntityFromDto(req, actual);
        
        if (actual.getNombre() != null) {
            actual.setNombre(actual.getNombre().trim());
        }
        
        if (actual.getNacionalidad() != null) {
            actual.setNacionalidad(actual.getNacionalidad().trim());
        }
        
        Autor saved = autorRepo.save(actual);
        log.info("✓ Autor actualizado id={}, nombre='{}'", saved.getId(), saved.getNombre());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public void eliminar(Long id) {
        log.info("Eliminando autor id={}", id);
        
        Autor autor = autorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor no encontrado: " + id));
        
        // Validar que no tenga libros asociados
        if (autor.getLibros() != null && !autor.getLibros().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar el autor '" + autor.getNombre() + "'. Tiene " + autor.getLibros().size() + " libro(s) asociado(s)"
            );
        }
        
        autorRepo.delete(autor);
        log.info("✓ Autor eliminado id={}, nombre='{}'", id, autor.getNombre());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AutorResponseDto obtener(Long id) {
        log.info("Obteniendo autor id={}", id);
        
        Autor autor = autorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor no encontrado: " + id));
        
        return mapper.toResponseDto(autor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AutorResponseDto> listar() {
        log.info("Listando todos los autores");
        
        return autorRepo.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AutorResponseDto> buscarPorNombre(String nombre) {
        log.info("Buscando autores por nombre: {}", nombre);
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre de búsqueda no puede estar vacío");
        }
        
        return autorRepo.findByNombreContainingIgnoreCase(nombre.trim()).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}