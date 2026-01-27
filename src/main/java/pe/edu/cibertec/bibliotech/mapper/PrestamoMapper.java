package pe.edu.cibertec.bibliotech.mapper;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import pe.edu.cibertec.bibliotech.api.request.PrestamoRequestDto;
import pe.edu.cibertec.bibliotech.api.response.PrestamoResponseDto;
import pe.edu.cibertec.bibliotech.entity.Libro;
import pe.edu.cibertec.bibliotech.entity.Prestamo;
import pe.edu.cibertec.bibliotech.entity.Usuario;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrestamoMapper {
    
    // Request DTO a Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "libro", source = "libroId", qualifiedByName = "idToLibro")
    @Mapping(target = "usuario", source = "usuarioId", qualifiedByName = "idToUsuario")
    @Mapping(target = "fechaPrestamo", expression = "java(getCurrentDate())")
    @Mapping(target = "fechaDevolucionEsperada", 
             expression = "java(dto.getFechaDevolucionEsperada() != null ? dto.getFechaDevolucionEsperada() : getDefaultDevolucionDate())")
    @Mapping(target = "estado", constant = "ACTIVO")
    Prestamo toEntity(PrestamoRequestDto dto);
    
    // Entity a Response DTO
    @Mapping(target = "libroId", source = "libro.id")
    @Mapping(target = "libroTitulo", source = "libro.titulo")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNombreCompleto", expression = "java(entity.getUsuario().getNombreCompleto())")
    @Mapping(target = "estado", expression = "java(entity.getEstado().name())")
    @Mapping(target = "vencido", expression = "java(entity.isVencido())")
    @Mapping(target = "diasAtraso", expression = "java(entity.getDiasAtraso())")
    PrestamoResponseDto toResponseDto(Prestamo entity);
    
    // Métodos auxiliares
    @Named("idToLibro")
    default Libro idToLibro(Long libroId) {
        if (libroId == null) return null;
        Libro libro = new Libro();
        libro.setId(libroId);
        return libro;
    }
    
    @Named("idToUsuario")
    default Usuario idToUsuario(Long usuarioId) {
        if (usuarioId == null) return null;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        return usuario;
    }
    
    default LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    default LocalDate getDefaultDevolucionDate() {
        return LocalDate.now().plusDays(14);
    }
}