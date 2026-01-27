package pe.edu.cibertec.bibliotech.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.cibertec.bibliotech.api.request.PrestamoRequestDto;
import pe.edu.cibertec.bibliotech.api.response.PrestamoResponseDto;
import pe.edu.cibertec.bibliotech.entity.Libro;
import pe.edu.cibertec.bibliotech.entity.Prestamo;
import pe.edu.cibertec.bibliotech.entity.Prestamo.EstadoPrestamo;
import pe.edu.cibertec.bibliotech.entity.Usuario;
import pe.edu.cibertec.bibliotech.exception.BusinessException;
import pe.edu.cibertec.bibliotech.exception.NotFoundException;
import pe.edu.cibertec.bibliotech.mapper.PrestamoMapper;
import pe.edu.cibertec.bibliotech.repository.LibroRepository;
import pe.edu.cibertec.bibliotech.repository.PrestamoRepository;
import pe.edu.cibertec.bibliotech.repository.UsuarioRepository;
import pe.edu.cibertec.bibliotech.service.PrestamoService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrestamoServiceImpl implements PrestamoService {
    
    private final PrestamoRepository prestamoRepo;
    private final LibroRepository libroRepo;
    private final UsuarioRepository usuarioRepo;
    private final PrestamoMapper mapper;
    
    /**
     * OPERACIÓN TRANSACCIONAL CRÍTICA: 
     * Registra un préstamo y reduce el stock del libro de forma atómica.
     */
    @Override
    public PrestamoResponseDto registrarPrestamo(PrestamoRequestDto req) {
        log.info("Registrando préstamo: libroId={}, usuarioId={}", req.getLibroId(), req.getUsuarioId());
        
        // 1. Validar que el libro existe y cargar la entidad completa
        Libro libro = libroRepo.findById(req.getLibroId())
                .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + req.getLibroId()));
        
        // 2. Validar que hay stock disponible
        if (!libro.isDisponible()) {
            throw new BusinessException(
                "El libro '" + libro.getTitulo() + "' no está disponible. Stock disponible: " + libro.getCantidadDisponible()
            );
        }
        
        // 3. Validar que el usuario existe y está activo
        Usuario usuario = usuarioRepo.findById(req.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + req.getUsuarioId()));
        
        if (!usuario.getActivo()) {
            throw new BusinessException("El usuario " + usuario.getNombreCompleto() + " está inactivo y no puede realizar préstamos");
        }
        
        // 4. Validar que el usuario no tenga préstamos vencidos
        List<Prestamo> prestamosActivosUsuario = prestamoRepo.findPrestamosActivosPorUsuario(usuario.getId());
        
        long prestamosVencidos = prestamosActivosUsuario.stream()
                .filter(Prestamo::isVencido)
                .count();
        
        if (prestamosVencidos > 0) {
            throw new BusinessException(
                "El usuario tiene " + prestamosVencidos + " préstamo(s) vencido(s). Debe devolverlos antes de solicitar un nuevo préstamo"
            );
        }
        
        // 5. Validar límite de préstamos simultáneos (máximo 3 por usuario)
        long prestamosActivos = prestamosActivosUsuario.size();
        if (prestamosActivos >= 3) {
            throw new BusinessException(
                "El usuario ya tiene " + prestamosActivos + " préstamos activos. Límite máximo: 3"
            );
        }
        
        // 6. Validar que el usuario no tenga ya este mismo libro prestado
        boolean yaTieneLibro = prestamosActivosUsuario.stream()
                .anyMatch(p -> p.getLibro().getId().equals(libro.getId()));
        
        if (yaTieneLibro) {
            throw new BusinessException(
                "El usuario ya tiene prestado el libro '" + libro.getTitulo() + "'"
            );
        }
        
        // 7. Crear el préstamo
        Prestamo prestamo = mapper.toEntity(req);
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        
        if (prestamo.getFechaDevolucionEsperada() == null) {
            prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(14));
        }
        
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        
        // 8. OPERACIÓN CRÍTICA: Reducir stock del libro de forma atómica
        Integer stockAnterior = libro.getCantidadDisponible();
        libro.setCantidadDisponible(stockAnterior - 1);
        
        // 9. Guardar ambas entidades en la misma transacción
        libroRepo.save(libro);
        Prestamo saved = prestamoRepo.save(prestamo);
        
        log.info("✓ Préstamo registrado - ID: {}, Libro: '{}', Stock anterior: {}, Stock actual: {}", 
                 saved.getId(), libro.getTitulo(), stockAnterior, libro.getCantidadDisponible());
        
        return mapper.toResponseDto(saved);
    }
    
    /**
     * OPERACIÓN TRANSACCIONAL CRÍTICA:
     * Registra la devolución de un libro y aumenta el stock de forma atómica.
     */
    @Override
    public PrestamoResponseDto registrarDevolucion(Long prestamoId, String observaciones) {
        log.info("Registrando devolución: prestamoId={}", prestamoId);
        
        // 1. Validar que el préstamo existe
        Prestamo prestamo = prestamoRepo.findById(prestamoId)
                .orElseThrow(() -> new NotFoundException("Préstamo no encontrado: " + prestamoId));
        
        // 2. Validar que el préstamo está en estado válido para devolución
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessException(
                "El préstamo ya fue devuelto el " + prestamo.getFechaDevolucionReal()
            );
        }
        
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO && prestamo.getEstado() != EstadoPrestamo.VENCIDO) {
            throw new BusinessException(
                "El préstamo no está en un estado válido para devolución. Estado actual: " + prestamo.getEstado()
            );
        }
        
        // 3. Obtener el libro asociado
        Libro libro = prestamo.getLibro();
        Integer stockAnterior = libro.getCantidadDisponible();
        
        // 4. Actualizar el préstamo
        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        
        // 5. Agregar observaciones si se proporcionaron
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            String obsActuales = prestamo.getObservaciones();
            if (obsActuales != null && !obsActuales.trim().isEmpty()) {
                prestamo.setObservaciones(obsActuales + " | Devolución: " + observaciones.trim());
            } else {
                prestamo.setObservaciones("Devolución: " + observaciones.trim());
            }
        }
        
        // 6. OPERACIÓN CRÍTICA: Aumentar stock del libro de forma atómica
        libro.setCantidadDisponible(stockAnterior + 1);
        
        // Validar que no exceda el total
        if (libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            throw new BusinessException(
                "Error: La cantidad disponible no puede exceder la cantidad total del libro"
            );
        }
        
        // 7. Guardar ambas entidades en la misma transacción
        libroRepo.save(libro);
        Prestamo saved = prestamoRepo.save(prestamo);
        
        long diasAtraso = saved.getDiasAtraso();
        String mensajeAtraso = diasAtraso > 0 ? " (con " + diasAtraso + " día(s) de atraso)" : " (a tiempo)";
        
        log.info("✓ Devolución registrada - ID: {}, Libro: '{}', Stock anterior: {}, Stock actual: {}{}", 
                 saved.getId(), libro.getTitulo(), stockAnterior, libro.getCantidadDisponible(), mensajeAtraso);
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public void cancelar(Long id) {
        log.info("Cancelando préstamo id={}", id);
        
        Prestamo prestamo = prestamoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Préstamo no encontrado: " + id));
        
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new BusinessException(
                "Solo se pueden cancelar préstamos en estado ACTIVO. Estado actual: " + prestamo.getEstado()
            );
        }
        
        // Restaurar stock del libro
        Libro libro = prestamo.getLibro();
        Integer stockAnterior = libro.getCantidadDisponible();
        libro.setCantidadDisponible(stockAnterior + 1);
        
        prestamoRepo.delete(prestamo);
        libroRepo.save(libro);
        
        log.info("✓ Préstamo cancelado - ID: {}, Stock restaurado de {} a {}", 
                 id, stockAnterior, libro.getCantidadDisponible());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PrestamoResponseDto obtener(Long id) {
        log.info("Obteniendo préstamo id={}", id);
        
        Prestamo prestamo = prestamoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Préstamo no encontrado: " + id));
        
        return mapper.toResponseDto(prestamo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDto> listar() {
        log.info("Listando todos los préstamos");
        
        return prestamoRepo.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDto> listarActivos() {
        log.info("Listando préstamos activos");
        
        return prestamoRepo.findByEstado(EstadoPrestamo.ACTIVO).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDto> listarVencidos() {
        log.info("Listando préstamos vencidos");
        
        return prestamoRepo.findPrestamosVencidos(LocalDate.now()).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDto> listarPorUsuario(Long usuarioId) {
        log.info("Listando préstamos por usuario id={}", usuarioId);
        
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new NotFoundException("Usuario no encontrado: " + usuarioId);
        }
        
        return prestamoRepo.findByUsuarioId(usuarioId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDto> listarPorLibro(Long libroId) {
        log.info("Listando préstamos por libro id={}", libroId);
        
        if (!libroRepo.existsById(libroId)) {
            throw new NotFoundException("Libro no encontrado: " + libroId);
        }
        
        return prestamoRepo.findByLibroId(libroId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void actualizarEstadosVencidos() {
        log.info("Actualizando estados de préstamos vencidos");
        
        List<Prestamo> prestamosVencidos = prestamoRepo.findPrestamosVencidos(LocalDate.now());
        
        int actualizados = 0;
        for (Prestamo prestamo : prestamosVencidos) {
            if (prestamo.getEstado() == EstadoPrestamo.ACTIVO) {
                prestamo.setEstado(EstadoPrestamo.VENCIDO);
                actualizados++;
            }
        }
        
        if (actualizados > 0) {
            prestamoRepo.saveAll(prestamosVencidos);
            log.info("✓ Actualizados {} préstamos a estado VENCIDO", actualizados);
        } else {
            log.info("No hay préstamos que actualizar");
        }
    }
}