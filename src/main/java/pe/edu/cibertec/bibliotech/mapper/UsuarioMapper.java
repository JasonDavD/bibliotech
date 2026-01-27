package pe.edu.cibertec.bibliotech.mapper;

import java.time.LocalDateTime;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import pe.edu.cibertec.bibliotech.api.request.UsuarioRequestDto;
import pe.edu.cibertec.bibliotech.api.response.UsuarioResponseDto;
import pe.edu.cibertec.bibliotech.entity.Prestamo;
import pe.edu.cibertec.bibliotech.entity.Usuario;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {
    
    // Request DTO a Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", expression = "java(getCurrentDateTime())")
    @Mapping(target = "ultimaActualizacion", expression = "java(getCurrentDateTime())")
    @Mapping(target = "prestamos", ignore = true)
    Usuario toEntity(UsuarioRequestDto dto);
    
    // Entity a Response DTO
    @Mapping(target = "nombreCompleto", expression = "java(entity.getNombreCompleto())")
    @Mapping(target = "prestamosActivos", expression = "java(contarPrestamosActivos(entity))")
    UsuarioResponseDto toResponseDto(Usuario entity);
    
    // Update Entity desde DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "ultimaActualizacion", expression = "java(getCurrentDateTime())")
    @Mapping(target = "prestamos", ignore = true)
    void updateEntityFromDto(UsuarioRequestDto dto, @MappingTarget Usuario entity);
    
    // Métodos auxiliares
    default Integer contarPrestamosActivos(Usuario entity) {
        if (entity.getPrestamos() == null) return 0;
        return (int) entity.getPrestamos().stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO)
                .count();
    }
    
    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}