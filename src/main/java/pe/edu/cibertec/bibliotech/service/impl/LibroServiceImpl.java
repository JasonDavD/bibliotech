package pe.edu.cibertec.bibliotech.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.cibertec.bibliotech.api.request.LibroRequestDto;
import pe.edu.cibertec.bibliotech.api.response.LibroResponseDto;
import pe.edu.cibertec.bibliotech.entity.Autor;
import pe.edu.cibertec.bibliotech.entity.Categoria;
import pe.edu.cibertec.bibliotech.entity.Libro;
import pe.edu.cibertec.bibliotech.exception.BusinessException;
import pe.edu.cibertec.bibliotech.exception.NotFoundException;
import pe.edu.cibertec.bibliotech.mapper.LibroMapper;
import pe.edu.cibertec.bibliotech.repository.AutorRepository;
import pe.edu.cibertec.bibliotech.repository.CategoriaRepository;
import pe.edu.cibertec.bibliotech.repository.LibroRepository;
import pe.edu.cibertec.bibliotech.service.LibroService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LibroServiceImpl implements LibroService {
    
    private final LibroRepository libroRepo;
    private final AutorRepository autorRepo;
    private final CategoriaRepository categoriaRepo;
    private final LibroMapper mapper;
    
    @Override
    public LibroResponseDto crear(LibroRequestDto req) {
        log.info("Creando libro: titulo='{}'", req.getTitulo());
        
        // Validar ISBN único
        libroRepo.findByIsbn(req.getIsbn().trim().toUpperCase())
                .ifPresent(libro -> {
                    throw new BusinessException("Ya existe un libro con el ISBN: " + req.getIsbn());
                });
        
        // Validar que autor existe
        Autor autor = autorRepo.findById(req.getAutorId())
                .orElseThrow(() -> new NotFoundException("Autor no encontrado: " + req.getAutorId()));
        
        // Validar que categoría existe
        Categoria categoria = categoriaRepo.findById(req.getCategoriaId())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + req.getCategoriaId()));
        
        Libro entity = mapper.toEntity(req);
        entity.setTitulo(req.getTitulo().trim());
        entity.setIsbn(req.getIsbn().trim().toUpperCase());
        entity.setAutor(autor);
        entity.setCategoria(categoria);
        
        Libro saved = libroRepo.save(entity);
        log.info("✓ Libro creado id={}", saved.getId());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public LibroResponseDto actualizar(Long id, LibroRequestDto req) {
        log.info("Actualizando libro id={}", id);
        
        Libro actual = libroRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + id));
        
        // Validar ISBN único (excepto el mismo libro)
        if (req.getIsbn() != null) {
            libroRepo.findByIsbn(req.getIsbn().trim().toUpperCase())
                    .ifPresent(libro -> {
                        if (!libro.getId().equals(id)) {
                            throw new BusinessException("Ya existe otro libro con el ISBN: " + req.getIsbn());
                        }
                    });
        }
        
        // Validar que no se reduzca la cantidad total por debajo de los préstamos activos
        if (req.getCantidadTotal() != null) {
            int prestados = actual.getCantidadTotal() - actual.getCantidadDisponible();
            if (req.getCantidadTotal() < prestados) {
                throw new BusinessException(
                    "No se puede reducir la cantidad total. Hay " + prestados + " ejemplares prestados"
                );
            }
        }
        
        // Validar autor
        if (req.getAutorId() != null) {
            Autor autor = autorRepo.findById(req.getAutorId())
                    .orElseThrow(() -> new NotFoundException("Autor no encontrado: " + req.getAutorId()));
            actual.setAutor(autor);
        }
        
        // Validar categoría
        if (req.getCategoriaId() != null) {
            Categoria categoria = categoriaRepo.findById(req.getCategoriaId())
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + req.getCategoriaId()));
            actual.setCategoria(categoria);
        }
        
        // Actualizar campos
        mapper.updateEntityFromDto(req, actual);
        
        if (actual.getTitulo() != null) {
            actual.setTitulo(actual.getTitulo().trim());
        }
        if (actual.getIsbn() != null) {
            actual.setIsbn(actual.getIsbn().trim().toUpperCase());
        }
        
        // Ajustar cantidad disponible si cambió la cantidad total
        if (req.getCantidadTotal() != null) {
            int diferencia = req.getCantidadTotal() - actual.getCantidadTotal();
            actual.setCantidadDisponible(actual.getCantidadDisponible() + diferencia);
            actual.setCantidadTotal(req.getCantidadTotal());
        }
        
        Libro saved = libroRepo.save(actual);
        log.info("✓ Libro actualizado id={}", saved.getId());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public void eliminar(Long id) {
        log.info("Eliminando libro id={}", id);
        
        Libro libro = libroRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + id));
        
        // Validar que no tenga préstamos activos
        long prestamosActivos = libro.getPrestamos().stream()
                .filter(p -> p.getEstado() == pe.edu.cibertec.bibliotech.entity.Prestamo.EstadoPrestamo.ACTIVO)
                .count();
        
        if (prestamosActivos > 0) {
            throw new BusinessException("No se puede eliminar. El libro tiene " + prestamosActivos + " préstamos activos");
        }
        
        libroRepo.delete(libro);
        log.info("✓ Libro eliminado id={}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LibroResponseDto obtener(Long id) {
        log.info("Obteniendo libro id={}", id);
        
        Libro libro = libroRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + id));
        
        return mapper.toResponseDto(libro);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDto> listar() {
        log.info("Listando todos los libros");
        
        return libroRepo.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDto> listarDisponibles() {
        log.info("Listando libros disponibles");
        
        return libroRepo.findLibrosDisponibles().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDto> buscarPorTitulo(String titulo) {
        log.info("Buscando libros por título: {}", titulo);
        
        return libroRepo.findByTituloContainingIgnoreCase(titulo).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDto> buscarPorAutor(Long autorId) {
        log.info("Buscando libros por autor id={}", autorId);
        
        if (!autorRepo.existsById(autorId)) {
            throw new NotFoundException("Autor no encontrado: " + autorId);
        }
        
        return libroRepo.findByAutorId(autorId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDto> buscarPorCategoria(Long categoriaId) {
        log.info("Buscando libros por categoría id={}", categoriaId);
        
        if (!categoriaRepo.existsById(categoriaId)) {
            throw new NotFoundException("Categoría no encontrada: " + categoriaId);
        }
        
        return libroRepo.findByCategoriaId(categoriaId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDto> buscarPorPalabra(String keyword) {
        log.info("Buscando libros por palabra clave: {}", keyword);
        
        return libroRepo.buscarPorTituloOAutor(keyword).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
}