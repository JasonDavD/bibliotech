package pe.edu.cibertec.bibliotech.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.cibertec.bibliotech.api.request.UsuarioRequestDto;
import pe.edu.cibertec.bibliotech.api.response.UsuarioResponseDto;
import pe.edu.cibertec.bibliotech.entity.Prestamo;
import pe.edu.cibertec.bibliotech.entity.Usuario;
import pe.edu.cibertec.bibliotech.exception.BusinessException;
import pe.edu.cibertec.bibliotech.exception.NotFoundException;
import pe.edu.cibertec.bibliotech.mapper.UsuarioMapper;
import pe.edu.cibertec.bibliotech.repository.UsuarioRepository;
import pe.edu.cibertec.bibliotech.service.UsuarioService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    
    private final UsuarioRepository usuarioRepo;
    private final UsuarioMapper mapper;
    
    @Override
    public UsuarioResponseDto crear(UsuarioRequestDto req) {
        log.info("Creando usuario: dni='{}', email='{}'", req.getDni(), req.getEmail());
        
        // Validar DNI único
        usuarioRepo.findByDni(req.getDni().trim())
                .ifPresent(usuario -> {
                    throw new BusinessException("Ya existe un usuario con el DNI: " + req.getDni());
                });
        
        // Validar email único
        usuarioRepo.findByEmail(req.getEmail().trim().toLowerCase())
                .ifPresent(usuario -> {
                    throw new BusinessException("Ya existe un usuario con el email: " + req.getEmail());
                });
        
        // Validar formato de DNI
        if (!req.getDni().matches("\\d{8}")) {
            throw new BusinessException("El DNI debe tener exactamente 8 dígitos numéricos");
        }
        
        Usuario entity = mapper.toEntity(req);
        entity.setNombre(req.getNombre().trim());
        entity.setApellido(req.getApellido().trim());
        entity.setDni(req.getDni().trim());
        entity.setEmail(req.getEmail().trim().toLowerCase());
        entity.setTelefono(req.getTelefono().trim());
        
        if (req.getDireccion() != null && !req.getDireccion().trim().isEmpty()) {
            entity.setDireccion(req.getDireccion().trim());
        }
        
        entity.setActivo(req.getActivo() != null ? req.getActivo() : true);
        entity.setFechaRegistro(LocalDateTime.now());
        entity.setUltimaActualizacion(LocalDateTime.now());
        
        Usuario saved = usuarioRepo.save(entity);
        log.info("✓ Usuario creado id={}, DNI={}", saved.getId(), saved.getDni());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto req) {
        log.info("Actualizando usuario id={}", id);
        
        Usuario actual = usuarioRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        
        // Validar DNI único (excepto el mismo usuario)
        if (req.getDni() != null && !req.getDni().trim().isEmpty()) {
            usuarioRepo.findByDni(req.getDni().trim())
                    .ifPresent(usuario -> {
                        if (!usuario.getId().equals(id)) {
                            throw new BusinessException("Ya existe otro usuario con el DNI: " + req.getDni());
                        }
                    });
        }
        
        // Validar email único (excepto el mismo usuario)
        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()) {
            usuarioRepo.findByEmail(req.getEmail().trim().toLowerCase())
                    .ifPresent(usuario -> {
                        if (!usuario.getId().equals(id)) {
                            throw new BusinessException("Ya existe otro usuario con el email: " + req.getEmail());
                        }
                    });
        }
        
        mapper.updateEntityFromDto(req, actual);
        
        if (actual.getNombre() != null) {
            actual.setNombre(actual.getNombre().trim());
        }
        if (actual.getApellido() != null) {
            actual.setApellido(actual.getApellido().trim());
        }
        if (actual.getDni() != null) {
            actual.setDni(actual.getDni().trim());
        }
        if (actual.getEmail() != null) {
            actual.setEmail(actual.getEmail().trim().toLowerCase());
        }
        if (actual.getTelefono() != null) {
            actual.setTelefono(actual.getTelefono().trim());
        }
        if (actual.getDireccion() != null) {
            actual.setDireccion(actual.getDireccion().trim());
        }
        
        actual.setUltimaActualizacion(LocalDateTime.now());
        
        Usuario saved = usuarioRepo.save(actual);
        log.info("✓ Usuario actualizado id={}", saved.getId());
        
        return mapper.toResponseDto(saved);
    }
    
    @Override
    public void eliminar(Long id) {
        log.info("Eliminando usuario id={}", id);
        
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        
        // Validar que no tenga préstamos activos
        long prestamosActivos = usuario.getPrestamos().stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO || 
                            p.getEstado() == Prestamo.EstadoPrestamo.VENCIDO)
                .count();
        
        if (prestamosActivos > 0) {
            throw new BusinessException(
                "No se puede eliminar el usuario. Tiene " + prestamosActivos + " préstamo(s) activo(s) o vencido(s)"
            );
        }
        
        usuarioRepo.delete(usuario);
        log.info("✓ Usuario eliminado id={}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto obtener(Long id) {
        log.info("Obteniendo usuario id={}", id);
        
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        
        return mapper.toResponseDto(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listar() {
        log.info("Listando todos los usuarios");
        
        return usuarioRepo.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarActivos() {
        log.info("Listando usuarios activos");
        
        return usuarioRepo.findByActivoTrue().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto buscarPorDni(String dni) {
        log.info("Buscando usuario por DNI: {}", dni);
        
        if (dni == null || dni.trim().isEmpty()) {
            throw new BusinessException("El DNI no puede estar vacío");
        }
        
        if (!dni.matches("\\d{8}")) {
            throw new BusinessException("El DNI debe tener exactamente 8 dígitos numéricos");
        }
        
        Usuario usuario = usuarioRepo.findByDni(dni.trim())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con DNI: " + dni));
        
        return mapper.toResponseDto(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto buscarPorEmail(String email) {
        log.info("Buscando usuario por email: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("El email no puede estar vacío");
        }
        
        Usuario usuario = usuarioRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con email: " + email));
        
        return mapper.toResponseDto(usuario);
    }
    
    @Override
    public void activar(Long id) {
        log.info("Activando usuario id={}", id);
        
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        
        if (usuario.getActivo()) {
            log.warn("El usuario id={} ya estaba activo", id);
            return;
        }
        
        usuario.setActivo(true);
        usuario.setUltimaActualizacion(LocalDateTime.now());
        usuarioRepo.save(usuario);
        
        log.info("✓ Usuario activado id={}", id);
    }
    
    @Override
    public void desactivar(Long id) {
        log.info("Desactivando usuario id={}", id);
        
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        
        // Validar que no tenga préstamos activos o vencidos
        long prestamosActivos = usuario.getPrestamos().stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO || 
                            p.getEstado() == Prestamo.EstadoPrestamo.VENCIDO)
                .count();
        
        if (prestamosActivos > 0) {
            throw new BusinessException(
                "No se puede desactivar el usuario. Tiene " + prestamosActivos + " préstamo(s) pendiente(s). " +
                "Debe devolver todos los libros antes de desactivar la cuenta"
            );
        }
        
        if (!usuario.getActivo()) {
            log.warn("El usuario id={} ya estaba inactivo", id);
            return;
        }
        
        usuario.setActivo(false);
        usuario.setUltimaActualizacion(LocalDateTime.now());
        usuarioRepo.save(usuario);
        
        log.info("✓ Usuario desactivado id={}", id);
    }
}