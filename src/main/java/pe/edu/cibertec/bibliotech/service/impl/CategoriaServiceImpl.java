package pe.edu.cibertec.bibliotech.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.cibertec.bibliotech.api.request.CategoriaRequestDto;
import pe.edu.cibertec.bibliotech.api.response.CategoriaResponseDto;
import pe.edu.cibertec.bibliotech.entity.Categoria;
import pe.edu.cibertec.bibliotech.exception.BusinessException;
import pe.edu.cibertec.bibliotech.exception.NotFoundException;
import pe.edu.cibertec.bibliotech.mapper.CategoriaMapper;
import pe.edu.cibertec.bibliotech.repository.CategoriaRepository;
import pe.edu.cibertec.bibliotech.service.CategoriaService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {
    
    private final CategoriaRepository categoriaRepo;
    private final CategoriaMapper mapper;
    
    @Override
    public CategoriaResponseDto crear(CategoriaRequestDto req) {
        log.info("Creando categoría: nombre='{}'", req.getNombre());
        
        // Validar que el nombre sea único
        categoriaRepo.findByNombre(req.getNombre().trim())
                .ifPresent(categoria -> {
                    throw new BusinessException("Ya existe una categoría con el nombre: " + req.getNombre());
                });
        
        Categoria entity = mapper.toEntity(req);
        entity.setNombre(req.getNombre().trim());
        
        if (req.getDescripcion() != null && !req.getDescripcion().trim().isEmpty()) {
            entity.setDescripcion(req.getDescripcion().trim());
        }
        
        entity.setFechaRegistro(LocalDateTime.now());
        
        Categoria saved = categoriaRepo.save(entity);
        log.info("✓ Categoría creada id={}, nombre='{}'", saved.getId(), saved.getNombre());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public CategoriaResponseDto actualizar(Long id, CategoriaRequestDto req) {
        log.info("Actualizando categoría id={}", id);
        
        Categoria actual = categoriaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + id));
        
        // Validar nombre único (excepto la misma categoría)
        if (req.getNombre() != null && !req.getNombre().trim().isEmpty()) {
            categoriaRepo.findByNombre(req.getNombre().trim())
                    .ifPresent(categoria -> {
                        if (!categoria.getId().equals(id)) {
                            throw new BusinessException("Ya existe otra categoría con el nombre: " + req.getNombre());
                        }
                    });
        }
        
        mapper.updateEntityFromDto(req, actual);
        
        if (actual.getNombre() != null) {
            actual.setNombre(actual.getNombre().trim());
        }
        
        if (actual.getDescripcion() != null) {
            actual.setDescripcion(actual.getDescripcion().trim());
        }
        
        Categoria saved = categoriaRepo.save(actual);
        log.info("✓ Categoría actualizada id={}, nombre='{}'", saved.getId(), saved.getNombre());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public void eliminar(Long id) {
        log.info("Eliminando categoría id={}", id);
        
        Categoria categoria = categoriaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + id));
        
        // Validar que no tenga libros asociados
        if (categoria.getLibros() != null && !categoria.getLibros().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar la categoría '" + categoria.getNombre() + "'. Tiene " + categoria.getLibros().size() + " libro(s) asociado(s)"
            );
        }
        
        categoriaRepo.delete(categoria);
        log.info("✓ Categoría eliminada id={}, nombre='{}'", id, categoria.getNombre());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDto obtener(Long id) {
        log.info("Obteniendo categoría id={}", id);
        
        Categoria categoria = categoriaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + id));
        
        return mapper.toResponseDto(categoria);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> listar() {
        log.info("Listando todas las categorías");
        
        return categoriaRepo.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDto buscarPorNombre(String nombre) {
        log.info("Buscando categoría por nombre: {}", nombre);
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre de búsqueda no puede estar vacío");
        }
        
        Categoria categoria = categoriaRepo.findByNombre(nombre.trim())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con nombre: " + nombre));
        
        return mapper.toResponseDto(categoria);
    }
}